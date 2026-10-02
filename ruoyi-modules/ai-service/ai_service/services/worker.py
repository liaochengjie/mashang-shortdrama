from __future__ import annotations

import logging
import threading
import time
import uuid

from ai_service.schemas.rag import JobRequest
from ai_service.models.errors import LostLease, RagError, Superseded
from ai_service.utils.http import request

log = logging.getLogger(__name__)


class Camunda:
    def __init__(self, settings, worker_id):
        self.settings, self.worker_id = settings, worker_id

    def call(self, path, data):
        return request(self.settings.camunda_url.rstrip("/") + path, data, self.settings.camunda_token, timeout=15, attempts=1)

    def fetch(self):
        return self.call("/external-task/fetchAndLock", {"workerId": self.worker_id, "maxTasks": 1, "usePriority": True,
                         "topics": [{"topicName": "drama-rag-index", "processDefinitionKey": "DramaAuthProcessV2", "lockDuration": self.settings.lease_seconds * 1000,
                                     "variables": ["snapshotId", "dramaId", "sourceVersion", "pipelineVersion", "embeddingProfile"]}]})

    def renew(self, task):
        self.call("/external-task/" + task["id"] + "/extendLock", {"workerId": self.worker_id, "newDuration": self.settings.lease_seconds * 1000})

    def complete(self, task, superseded=False):
        self.call("/external-task/" + task["id"] + "/complete", {"workerId": self.worker_id,
                  "variables": {"ragSuperseded": {"value": superseded, "type": "Boolean"}}})

    def fail(self, task, error, attempts):
        available = task.get("retries")
        available = self.settings.max_attempts if available is None else available
        retries = max(0, min(available - 1, self.settings.max_attempts - attempts)) if error.retryable else 0
        self.call("/external-task/" + task["id"] + "/failure", {"workerId": self.worker_id,
                  "errorMessage": error.code, "retries": retries, "retryTimeout": min(60000, 5000 * 2 ** min(attempts, 4))})


class Ownership:
    def __init__(self, store, job, settings, camunda=None, task=None):
        self.store, self.job, self.settings, self.camunda, self.task = store, job, settings, camunda, task
        self.stop, self.lost = threading.Event(), threading.Event()
        self.thread = threading.Thread(target=self.heartbeat, daemon=True)

    def heartbeat(self):
        while not self.stop.wait(min(20, self.settings.lease_seconds / 3)):
            try:
                if self.task:
                    self.camunda.renew(self.task)
                self.store.renew(self.job, self.settings.lease_seconds)
            except Exception:
                self.lost.set()
                return

    def guard(self):
        if self.lost.is_set():
            raise LostLease()
        if self.task:
            try:
                self.camunda.renew(self.task)
            except Exception:
                self.lost.set()
                raise LostLease() from None
        with self.store.tx() as c:
            self.store.guard(c, self.job)

    def __enter__(self):
        self.thread.start()
        return self

    def __exit__(self, *args):
        self.stop.set()
        self.thread.join(timeout=20)


class Worker:
    def __init__(self, settings, store, pipeline):
        self.settings, self.store, self.pipeline = settings, store, pipeline
        self.owner = "rag-" + str(uuid.uuid4())
        self.camunda = Camunda(settings, self.owner)

    def once(self, standalone=False):
        task = None
        if standalone:
            job = self.store.claim(self.owner, self.settings.lease_seconds)
        else:
            tasks = self.camunda.fetch()
            if not tasks:
                return False
            task = tasks[0]
            values = {k: v["value"] for k, v in task["variables"].items()}
            try:
                req = JobRequest.model_validate(values)
            except ValueError:
                self.camunda.fail(task, RagError("INVALID_TASK_CONTRACT"), self.settings.max_attempts)
                return True
            try:
                accepted = self.store.accept(req)
                self.store.bind_external(accepted["job_id"], task["id"])
            except RagError as error:
                self.camunda.fail(task, error, self.settings.max_attempts)
                return True
            if accepted["state"] == "SUPERSEDED":
                self.camunda.complete(task, True)
                return True
            if accepted["state"] == "FAILED":
                self.camunda.fail(task, RagError(accepted["error"] or "JOB_FAILED"), accepted["attempts"])
                return True
            job = self.store.claim(self.owner, self.settings.lease_seconds, accepted["job_id"])
        if not job:
            return False
        with Ownership(self.store, job, self.settings, self.camunda, task) as ownership:
            try:
                self.pipeline.run(job, ownership.guard)
                ownership.guard()
                if task:
                    self.camunda.renew(task)
                    ownership.guard()
                    self.camunda.complete(task)
                self.store.state(job, "READY", "registered-and-completed")
            except LostLease:
                log.warning("lost lease job=%s", job["job_id"])
            except Superseded:
                ownership.guard()
                if task:
                    self.camunda.complete(task, True)
                self.store.state(job, "SUPERSEDED")
            except Exception as exc:
                error = exc if isinstance(exc, RagError) else RagError("UNEXPECTED_WORKER_ERROR", True)
                ownership.guard()
                retry = error.retryable and job["attempts"] < self.settings.max_attempts
                if task:
                    self.camunda.fail(task, error, job["attempts"])
                self.store.state(job, "RETRYABLE_FAILED" if retry else "FAILED", error=error.code, delay=10 * job["attempts"])
                log.warning("job=%s error=%s", job["job_id"], error.code)
        return True

    def run(self, standalone=False):
        while True:
            try:
                busy = self.once(standalone)
            except Exception as exc:
                log.warning("worker cycle failed: %s", exc.code if isinstance(exc, RagError) else type(exc).__name__)
                busy = False
            if not busy:
                time.sleep(3)

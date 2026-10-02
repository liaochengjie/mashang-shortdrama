"""Application operations shared by HTTP controllers."""
from ai_service.models.errors import RagError
from ai_service.services.search import Search
from ai_service.utils.doctor import model_configuration


class RagService:
    def __init__(self, settings, store, index, content, models):
        self.settings = settings
        self.store = store
        self.index = index
        self.content = content
        self.models = models

    def ready(self):
        if not self.settings.internal_token or model_configuration(self.settings):
            raise RagError("CONFIGURATION_MISSING")
        self.store.health()
        self.index().health()
        self.content.call("/internal/rag/health")
        return {"status": "UP", "embeddingProfile": self.settings.profile}

    def accept(self, request):
        if request.pipeline_version != self.settings.pipeline_version or request.embedding_profile != self.settings.profile:
            raise RagError("PIPELINE_PROFILE_MISMATCH", status=409)
        job = self.store.accept(request)
        return {"jobId": job["job_id"], "buildId": job["build_id"], "state": job["state"]}

    def status(self, job_id):
        job = self.store.get(job_id)
        return {"jobId": job["job_id"], "buildId": job["build_id"], "state": job["state"],
                "progress": job["progress"], "error": job["error"], "attempts": job["attempts"]}

    def retry(self, job_id):
        self.store.get(job_id)
        return {"changed": self.store.retry(job_id),
                "camundaAction": "If external task has retries=0, restore its retries separately after resolving the incident"}

    def search(self, request):
        return Search(self.settings, self.store, self.models, self.index(), self.content).run(request)

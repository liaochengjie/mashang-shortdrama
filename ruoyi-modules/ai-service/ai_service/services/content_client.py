"""Content-service authority adapter."""

from ai_service.models.errors import RagError
from ai_service.utils.http import request


class ContentClient:
    def __init__(self, settings):
        self.settings = settings

    def call(self, path, body=None, method=None):
        return request(self.settings.content_url.rstrip("/") + path, body, self.settings.internal_token, method, timeout=15, content_conflicts=True)

    def active_builds(self, profile):
        from urllib.parse import quote
        ids, cursor = [], "0"
        for _ in range(20):
            data = self.call("/internal/rag/published-builds?profile=" + quote(profile) + "&after=" + cursor)
            ids.extend(i["buildId"] for i in data["items"])
            if not data.get("next"):
                return ids
            cursor = data["next"]
        raise RagError("PUBLISHED_BUILD_WINDOW_EXCEEDED")

    def build_status(self, build_id):
        from urllib.parse import quote
        return self.call("/internal/rag/builds/" + quote(build_id, safe=""))

    def snapshot(self, snapshot_id):
        return self.call("/internal/rag/snapshots/" + snapshot_id)

    def validate(self, candidates):
        return self.call("/internal/rag/content/validate", {"candidates": candidates})["items"]

    def register(self, job, manifest):
        return self.call("/internal/rag/index-results/" + job["build_id"], {
            "snapshotId": job["snapshot_id"], "dramaId": job["drama_id"], "sourceVersion": job["source_version"],
            "embeddingProfile": job["profile"], "pipelineVersion": job["pipeline_version"], "state": "READY", **manifest,
        }, "PUT")

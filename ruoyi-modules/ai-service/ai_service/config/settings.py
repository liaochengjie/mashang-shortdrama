from __future__ import annotations

import hashlib
import os
from dataclasses import dataclass, field
from pathlib import Path


def environment() -> dict[str, str]:
    values = {}
    path = Path(__file__).resolve().parents[2] / ".env"
    if path.exists():
        for line in path.read_text(encoding="utf-8-sig").splitlines():
            line = line.strip()
            if line and not line.startswith("#") and "=" in line:
                key, value = line.split("=", 1)
                values[key.strip()] = value.strip().strip("\"'")
    values.update(os.environ)
    return values


@dataclass(frozen=True)
class Settings:
    api_key: str = field(repr=False, default="")
    internal_token: str = field(repr=False, default="")
    camunda_token: str = field(repr=False, default="")
    database_url: str = field(repr=False, default="sqlite:///data/rag.db")
    base_url: str = "https://dashscope.aliyuncs.com/compatible-mode/v1"
    rerank_url: str = "https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank"
    workspace_id: str = ""
    text_model: str = "qwen3.8-flash"
    multimodal_model: str = "qwen3.8-omni-flash"
    embedding_model: str = "qwen3.7-text-embedding"
    rerank_model: str = "qwen3.7-text-rerank"
    dimensions: int = 1024
    content_url: str = "http://127.0.0.1:10001"
    camunda_url: str = "http://127.0.0.1:8088/engine-rest"
    milvus_uri: str = "http://127.0.0.1:19530"
    milvus_token: str = field(repr=False, default="")
    pipeline_version: str = "evidence-v1"
    media_hosts: tuple[str, ...] = ("localhost", "127.0.0.1", "minio")
    data_dir: Path = Path("data")
    timeout: int = 60
    segment_seconds: int = 20
    max_media_bytes: int = 200_000_000
    max_duration: int = 3600
    max_attempts: int = 3
    lease_seconds: int = 120
    session_ttl: int = 600
    candidate_window: int = 100
    min_rerank_score: float = 0.2

    @property
    def profile(self) -> str:
        spec = f"{self.embedding_model}|{self.dimensions}|COSINE|raw|text-v1"
        return "p_" + hashlib.sha256(spec.encode()).hexdigest()[:20]

    @classmethod
    def load(cls) -> Settings:
        e = environment()
        workspace = e.get("BAILIAN_WORKSPACE_ID", "").strip()
        domain = f"https://{workspace}.cn-beijing.maas.aliyuncs.com" if workspace else ""
        base = e.get("BAILIAN_BASE_URL", "").strip()
        rerank = e.get("BAILIAN_RERANK_URL", "").strip()
        if domain:
            if not base or base == cls.base_url:
                base = domain + "/compatible-mode/v1"
            if not rerank or rerank == cls.rerank_url:
                rerank = domain + "/api/v1/services/rerank/text-rerank/text-rerank"
        return cls(
            api_key=e.get("DASHSCOPE_API_KEY", ""), internal_token=e.get("RAG_INTERNAL_TOKEN", ""),
            camunda_token=e.get("CAMUNDA_WORKER_TOKEN", ""),
            database_url=e.get("RAG_DATABASE_URL", "sqlite:///data/rag.db"),
            base_url=base or cls.base_url, rerank_url=rerank or cls.rerank_url, workspace_id=workspace,
            text_model=e.get("BAILIAN_TEXT_MODEL", cls.text_model), multimodal_model=e.get("BAILIAN_MULTIMODAL_MODEL", cls.multimodal_model),
            embedding_model=e.get("BAILIAN_EMBEDDING_MODEL", cls.embedding_model), rerank_model=e.get("BAILIAN_RERANK_MODEL", cls.rerank_model),
            dimensions=int(e.get("BAILIAN_EMBEDDING_DIMENSIONS", "1024")),
            content_url=e.get("CONTENT_SERVICE_URL", cls.content_url), camunda_url=e.get("CAMUNDA_REST_URL", cls.camunda_url),
            milvus_uri=e.get("MILVUS_URI", cls.milvus_uri), milvus_token=e.get("MILVUS_TOKEN", ""),
            pipeline_version=e.get("RAG_PIPELINE_VERSION", cls.pipeline_version),
            media_hosts=tuple(x.strip() for x in e.get("RAG_MEDIA_ALLOWED_HOSTS", "localhost,127.0.0.1,minio").split(",") if x.strip()),
            data_dir=Path(e.get("RAG_DATA_DIR", "data")), timeout=int(e.get("BAILIAN_TIMEOUT_SECONDS", "60")),
            segment_seconds=int(e.get("RAG_SEGMENT_SECONDS", "20")),
            min_rerank_score=float(e.get("RAG_MIN_RERANK_SCORE", "0.2")),
        )


APP_NAME = "Kcat AI Service"
APP_VERSION = "0.1.0"
HOST = "127.0.0.1"
PORT = 7777

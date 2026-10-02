"""Lazy service composition; importing the API performs no external I/O."""
from functools import lru_cache

from ai_service.config.settings import Settings
from ai_service.repositories.task_repository import Store
from ai_service.repositories.vector_repository import MilvusIndex
from ai_service.services.bailian import Bailian
from ai_service.services.content_client import ContentClient
from ai_service.services.rag_service import RagService

settings = Settings.load()
store = Store(settings.database_url)


@lru_cache
def index():
    return MilvusIndex(settings)


def service():
    return RagService(settings, store, index, ContentClient(settings), Bailian(settings))

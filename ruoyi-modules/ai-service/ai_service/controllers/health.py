"""Process and dependency health endpoints."""
from fastapi import APIRouter, Depends
from ai_service.services.runtime import service
from ai_service.services.rag_service import RagService

router = APIRouter(tags=["health"])


@router.get("/health")
@router.get("/health/live")
def live():
    return {"status": "UP"}


@router.get("/health/ready")
def ready(rag: RagService = Depends(service)):
    return rag.ready()

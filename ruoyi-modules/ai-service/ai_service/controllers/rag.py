"""Authenticated indexing and search endpoints."""
import hmac
from fastapi import APIRouter, Depends, Header

from ai_service.models.errors import RagError
from ai_service.schemas.rag import JobRequest, SearchRequest
from ai_service.services import runtime
from ai_service.services.rag_service import RagService


def identity(authorization: str = Header(default="")):
    token = runtime.settings.internal_token
    if not token or not hmac.compare_digest(authorization, "Bearer " + token):
        raise RagError("UNAUTHORIZED", status=401)


router = APIRouter(prefix="/internal", dependencies=[Depends(identity)], tags=["rag"])


@router.post("/index-jobs", status_code=202)
def accept(request: JobRequest, rag: RagService = Depends(runtime.service)):
    return rag.accept(request)


@router.get("/index-jobs/{job_id}")
def status(job_id: str, rag: RagService = Depends(runtime.service)):
    return rag.status(job_id)


@router.post("/index-jobs/{job_id}/retry")
def retry(job_id: str, rag: RagService = Depends(runtime.service)):
    return rag.retry(job_id)


@router.post("/search/dramas")
def search(request: SearchRequest, rag: RagService = Depends(runtime.service)):
    return rag.search(request)

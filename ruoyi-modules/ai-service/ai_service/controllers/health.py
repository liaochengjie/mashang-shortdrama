"""Process health endpoint."""

from fastapi import APIRouter


router = APIRouter(tags=["health"])


@router.get("/health")
def health() -> dict[str, str]:
    """Report process health without calling external dependencies."""
    return {"status": "ok", "service": "ai-service"}

"""Create the HTTP application and register controllers."""
from fastapi import FastAPI
from fastapi.responses import JSONResponse

from ai_service.config.settings import APP_NAME, APP_VERSION
from ai_service.controllers.health import router as health_router
from ai_service.controllers.rag import router as rag_router
from ai_service.models.errors import RagError

app = FastAPI(title=APP_NAME, version=APP_VERSION)
app.include_router(health_router)
app.include_router(rag_router)


@app.exception_handler(RagError)
async def rag_error(_, error):
    return JSONResponse(status_code=error.status, content={"error": error.code, "retryable": error.retryable})


@app.exception_handler(Exception)
async def unknown_error(_, error):
    return JSONResponse(status_code=503, content={"error": "DEPENDENCY_UNAVAILABLE", "retryable": True})

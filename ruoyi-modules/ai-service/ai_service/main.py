"""Create the HTTP application and register controllers."""

from fastapi import FastAPI

from ai_service.config.settings import APP_NAME, APP_VERSION
from ai_service.controllers.health import router as health_router


app = FastAPI(
    title=APP_NAME,
    description="AI module powered by LangChain and LangGraph.",
    version=APP_VERSION,
)
app.include_router(health_router)

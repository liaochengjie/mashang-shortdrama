"""Start the AI service with python -m ai_service."""

import uvicorn

from ai_service.config.settings import HOST, PORT


if __name__ == "__main__":
    uvicorn.run("ai_service.main:app", host=HOST, port=PORT)

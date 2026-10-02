"""Local preflight only: no provider request, Docker operation or secret output."""
import shutil
import sys
from ai_service.config.settings import Settings

def model_configuration(settings):
    issues = []
    if not settings.api_key:
        issues.append("DASHSCOPE_API_KEY missing")
    if settings.base_url == Settings.base_url or settings.rerank_url == Settings.rerank_url:
        issues.append("Deferred configuration: fill BAILIAN_WORKSPACE_ID or BOTH business-space endpoint URLs before integration")
    if settings.dimensions != 1024:
        issues.append("Selected deployment profile requires 1024 embedding dimensions")
    return issues

def check(settings):
    checks = {
        "python312": sys.version_info[:2] == (3, 12),
        "internalTokenConfigured": bool(settings.internal_token),
        "camundaWorkerTokenConfigured": bool(settings.camunda_token),
        "dedicatedMysqlConfigured": settings.database_url.startswith("mysql://") and "CHANGE_LOCALLY" not in settings.database_url,
        "ffmpegAvailable": bool(shutil.which("ffmpeg")),
        "ffprobeAvailable": bool(shutil.which("ffprobe")),
    }
    return {"checks": checks, "modelConfigurationIssues": model_configuration(settings),
            "embeddingProfile": settings.profile, "pipelineVersion": settings.pipeline_version,
            "externalServices": "NOT_TESTED (run explicit integration scripts later)",
            "readyForIntegration": all(checks.values()) and not model_configuration(settings)}

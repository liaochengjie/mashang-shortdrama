from __future__ import annotations

import hashlib
import json
import math
import re
import subprocess
import uuid
from pathlib import Path
from urllib.parse import urlparse
from urllib.request import HTTPRedirectHandler, Request, build_opener

from ai_service.models.errors import RagError


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, *args, **kwargs):
        raise RagError("MEDIA_REDIRECT_REJECTED")


def download(media, target: Path, settings):
    u = urlparse(media.url)
    if u.scheme not in ("http", "https") or u.hostname not in settings.media_hosts or u.username or u.password:
        raise RagError("MEDIA_HOST_REJECTED", status=400)
    target.parent.mkdir(parents=True, exist_ok=True)
    if not target.exists():
        temporary = target.with_suffix("."+uuid.uuid4().hex+".part")
        try:
            with build_opener(NoRedirect()).open(Request(media.url), timeout=settings.timeout) as source, temporary.open("wb") as out:
                size = 0
                while block := source.read(1024 * 1024):
                    size += len(block)
                    if size > settings.max_media_bytes:
                        raise RagError("MEDIA_TOO_LARGE")
                    out.write(block)
            temporary.replace(target)
        except RagError:
            temporary.unlink(missing_ok=True)
            raise
        except Exception:
            temporary.unlink(missing_ok=True)
            raise RagError("MEDIA_DOWNLOAD_FAILED", True) from None
    actual = hashlib.sha256(target.read_bytes()).hexdigest()
    if media.sha256 and actual != media.sha256:
        raise RagError("MEDIA_IDENTITY_MISMATCH")
    return actual


def run(args):
    try:
        return subprocess.run(args, capture_output=True, check=True, timeout=120, text=True, encoding="utf-8", errors="replace")
    except (OSError, subprocess.SubprocessError):
        raise RagError("MEDIA_TOOL_FAILED") from None


def probe(path, max_duration):
    result = json.loads(run(["ffprobe", "-v", "error", "-show_format", "-show_streams", "-of", "json", str(path)]).stdout)
    duration = float(result["format"]["duration"])
    if not math.isfinite(duration) or not 0 < duration <= max_duration:
        raise RagError("INVALID_MEDIA_DURATION")
    if not any(s["codec_type"] == "video" for s in result["streams"]):
        raise RagError("VIDEO_STREAM_MISSING")
    return duration, any(s["codec_type"] == "audio" for s in result["streams"])


def segment(path, directory, start, length, has_audio):
    directory.mkdir(parents=True, exist_ok=True)
    clip = directory / f"{start:.3f}.mp4"
    if not clip.exists():
        temporary = directory / (uuid.uuid4().hex+".partial.mp4")
        run(["ffmpeg", "-y", "-v", "error", "-ss", str(start), "-i", str(path), "-t", str(length),
             "-vf", "scale=640:-2,fps=2", "-c:v", "libx264", "-crf", "28", "-an", str(temporary)])
        temporary.replace(clip)
    audio = directory / f"{start:.3f}.wav"
    silent = not has_audio
    if has_audio:
        if not audio.exists():
            temporary = directory / (uuid.uuid4().hex+".partial.wav")
            run(["ffmpeg", "-y", "-v", "error", "-ss", str(start), "-i", str(path), "-t", str(length),
                 "-vn", "-ar", "16000", "-ac", "1", str(temporary)])
            temporary.replace(audio)
        volume = run(["ffmpeg", "-i", str(audio), "-af", "volumedetect", "-f", "null", "-"]).stderr
        silent = "max_volume: -inf dB" in volume
    return clip, audio if has_audio and not silent else None


def subtitles(path: Path, episode_id, duration):
    text = path.read_text(encoding="utf-8-sig")
    pattern = r"(?m)((?:\d{1,2}:)?[0-5]\d:[0-5]\d[,.]\d{3})\s+-->\s+((?:\d{1,2}:)?[0-5]\d:[0-5]\d[,.]\d{3})[^\n]*\n(.*?)(?=\n\s*\n|\Z)"
    def seconds(v):
        parts = v.replace(",", ".").split(":")
        h, m, s = parts if len(parts)==3 else ["0",*parts]
        return int(h) * 3600 + int(m) * 60 + float(s)
    cues = []
    for i, match in enumerate(re.finditer(pattern, text, re.S)):
        a, b = seconds(match[1]), seconds(match[2])
        line = re.sub(r"<[^>]+>", "", match[3]).strip()
        if not line or not 0 <= a < b <= duration + 0.1:
            raise RagError("INVALID_SUBTITLE_CUE")
        cues.append({"id": f"subtitle:{episode_id}:{i}", "text": line, "startTime": a, "endTime": min(b, duration)})
    if not cues:
        raise RagError("SUBTITLE_FORMAT_UNSUPPORTED")
    return cues

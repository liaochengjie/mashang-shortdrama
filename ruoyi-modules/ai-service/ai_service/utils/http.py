from __future__ import annotations

import json
import random
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

from ai_service.models.errors import RagError, Superseded


def request(url, body=None, token="", method=None, timeout=30, stream=False, attempts=3, content_conflicts=False):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    payload = None if body is None else json.dumps(body, ensure_ascii=False, allow_nan=False).encode()
    for attempt in range(attempts):
        try:
            req = Request(url, data=payload, headers=headers, method=method or ("POST" if body is not None else "GET"))
            with urlopen(req, timeout=timeout) as response:
                if response.status == 204:
                    return {}
                if not stream:
                    data = json.load(response)
                    if isinstance(data, dict) and data.get("error"):
                        raise RagError("PROVIDER_ERROR")
                    return data
                parts, usage, rid = [], None, response.headers.get("x-request-id")
                complete = False
                for line in response:
                    line = line.decode().strip()
                    if line == "data: [DONE]":
                        complete = True
                        break
                    if not line.startswith("data:"):
                        continue
                    event = json.loads(line[5:])
                    if event.get("error"):
                        raise RagError("PROVIDER_STREAM_ERROR")
                    rid = event.get("id", rid)
                    usage = event.get("usage", usage)
                    for choice in event.get("choices", []):
                        complete = complete or bool(choice.get("finish_reason"))
                        value = choice.get("delta", {}).get("content")
                        if value:
                            parts.append(value)
                if not parts:
                    raise RagError("EMPTY_MODEL_OUTPUT")
                if not complete:
                    raise RagError("INCOMPLETE_MODEL_STREAM", True)
                return {"text": "".join(parts), "usage": usage, "requestId": rid}
        except HTTPError as exc:
            if exc.code == 409 and content_conflicts:
                # Only this explicit business conflict cancels the version's workflow.
                if "SUPERSEDED" in exc.read(8192).decode("utf-8", errors="replace"):
                    raise Superseded() from None
            retryable = exc.code == 429 or exc.code >= 500
            if not retryable or attempt == attempts - 1:
                raise RagError(f"HTTP_{exc.code}", retryable, exc.code if exc.code in (400, 401, 403, 404, 409) else 503) from None
            delay = min(30, float(exc.headers.get("Retry-After", "0")) if exc.headers.get("Retry-After", "0").isdigit() else 0)
        except (URLError, TimeoutError, OSError):
            if attempt == attempts - 1:
                raise RagError("NETWORK_ERROR", True) from None
            delay = 0
        except (ValueError, UnicodeError):
            raise RagError("INVALID_PROVIDER_RESPONSE") from None
        time.sleep(max(delay, 2 ** attempt + random.random()))

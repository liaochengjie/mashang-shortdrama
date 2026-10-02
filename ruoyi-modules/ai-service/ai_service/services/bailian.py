from __future__ import annotations

import base64
import json
import math
from pathlib import Path

from ai_service.models.errors import RagError
from ai_service.utils.http import request
from ai_service.schemas.rag import SceneOutput


class Bailian:
    def __init__(self, settings):
        self.settings = settings

    def credentials(self):
        if not self.settings.api_key:
            raise RagError("MODEL_KEY_MISSING", status=503)

    def chat(self, model, content):
        self.credentials()
        body = {"model": model, "messages": [{"role": "user", "content": content}],
                "stream": True, "stream_options": {"include_usage": True}, "max_tokens": 4096}
        if model == self.settings.multimodal_model:
            body["modalities"] = ["text"]
        result = request(self.settings.base_url.rstrip("/") + "/chat/completions", body,
                         self.settings.api_key, timeout=self.settings.timeout, stream=True)
        return {**result, "model": model}

    def video(self, path: Path):
        if path.stat().st_size > 8_000_000:
            raise RagError("VIDEO_SEGMENT_TOO_LARGE")
        media = "data:;base64," + base64.b64encode(path.read_bytes()).decode()
        return self.chat(self.settings.multimodal_model, [
            {"type": "video_url", "video_url": {"url": media}},
            {"type": "text", "text": "描述当前片段可见的动作、场景、人物互动和画面文字。未知身份用人物A等标记；不猜背景或精确时间。"},
        ])

    def audio(self, path: Path):
        media = base64.b64encode(path.read_bytes()).decode()
        return self.chat(self.settings.multimodal_model, [
            {"type": "input_audio", "input_audio": {"data": "data:audio/wav;base64," + media, "format": "wav"}},
            {"type": "text", "text": "逐字转写听到的对白。不添加剧情，不猜说话人身份，不提供时间。无可辨语音请明确说明。"},
        ])

    def fuse(self, observations, dialogue, background, schema):
        prompt = json.dumps({"instruction": "根据证据生成场景JSON。严格区分observation/dialogue/background/inference；每条sourceRefs只引用给定证据的id。不要猜精确时间，不把背景当成片段事实。只输出JSON。",
                             "visual": observations, "speech": dialogue, "background": background, "schema": schema}, ensure_ascii=False)
        result = self.chat(self.settings.text_model, prompt)
        for repair in range(2):
            try:
                raw = result["text"].strip()
                if raw.startswith("```"):
                    raw = raw.split("\n", 1)[1].rsplit("```", 1)[0]
                result["parsed"] = json.loads(raw)
                SceneOutput.model_validate(result["parsed"])
                return result
            except (ValueError, IndexError):
                if repair:
                    raise RagError("INVALID_MODEL_JSON_OR_SCHEMA") from None
                result = self.chat(self.settings.text_model, prompt + "\n上次输出不符合JSON Schema，请严格按schema输出合法对象。")

    def embed(self, texts):
        self.credentials()
        if not texts or any(not t.strip() for t in texts) or len(texts) > 10:
            raise RagError("INVALID_EMBEDDING_INPUT", status=400)
        data = request(self.settings.base_url.rstrip("/") + "/embeddings", {
            "model": self.settings.embedding_model, "input": texts, "dimensions": self.settings.dimensions, "encoding_format": "float",
        }, self.settings.api_key, timeout=self.settings.timeout)
        rows = sorted(data.get("data", []), key=lambda x: x["index"])
        if [r["index"] for r in rows] != list(range(len(texts))):
            raise RagError("EMBEDDING_COUNT_MISMATCH")
        vectors = [r["embedding"] for r in rows]
        if any(len(v) != self.settings.dimensions or not all(isinstance(n, (int, float)) and math.isfinite(n) for n in v)
               or sum(n * n for n in v) == 0 for v in vectors):
            raise RagError("EMBEDDING_PROFILE_MISMATCH")
        return {"vectors": vectors, "usage": data.get("usage"), "requestId": data.get("id"), "model": self.settings.embedding_model}

    def rerank(self, query, documents):
        self.credentials()
        data = request(self.settings.rerank_url, {"model": self.settings.rerank_model,
                       "input": {"query": query, "documents": documents},
                       "parameters": {"top_n": len(documents), "return_documents": False}},
                       self.settings.api_key, timeout=self.settings.timeout)
        results = data.get("output", {}).get("results", [])
        indices = [r.get("index") for r in results]
        if len(set(indices)) != len(indices) or any(not isinstance(i, int) or i < 0 or i >= len(documents) for i in indices):
            raise RagError("INVALID_RERANK_INDEX")
        if any(not isinstance(r.get("relevance_score"), (int, float)) or not math.isfinite(r["relevance_score"]) for r in results):
            raise RagError("INVALID_RERANK_SCORE")
        return results

from __future__ import annotations

import hashlib
import json
from pathlib import Path

from ai_service.schemas.rag import SceneOutput, Snapshot
from ai_service.models.errors import RagError, Superseded
from ai_service.services.media import download, probe, segment, subtitles


def digest(value):
    return hashlib.sha256(json.dumps(value, sort_keys=True, ensure_ascii=False, allow_nan=False).encode()).hexdigest()


def build_chunks(snapshot, job, episode, offset, length, scenes, refs):
    docs = []
    for scene_index, scene in enumerate(scenes.scenes):
        parent = digest([snapshot.source_hash, episode.episode_id, offset, scene_index])
        for evidence_index, evidence in enumerate(scene.evidence):
            if any(ref not in refs for ref in evidence.source_refs):
                raise RagError("UNKNOWN_EVIDENCE_SOURCE")
            expected = {"observation": "video", "dialogue": ("subtitle", "audio"), "background": "background"}
            if evidence.kind in expected:
                prefixes = expected[evidence.kind]
                prefixes = (prefixes,) if isinstance(prefixes, str) else prefixes
                if any(not r.startswith(prefixes) for r in evidence.source_refs):
                    raise RagError("EVIDENCE_SOURCE_TYPE_MISMATCH")
            cues = [refs[r] for r in evidence.source_refs if r.startswith("subtitle:")]
            trusted = evidence.kind == "dialogue" and len(cues) == len(evidence.source_refs) and not scene.uncertain
            # Only supplied subtitle timing is trusted. Model-proposed numbers are discarded.
            a = min(c["startTime"] for c in cues) if trusted else (offset if evidence.kind in ("observation", "dialogue") else None)
            b = max(c["endTime"] for c in cues) if trusted else (offset + length if a is not None else None)
            if trusted and not 0 <= a < b <= (episode.duration or offset + length) + 0.1:
                raise RagError("EVIDENCE_TIME_OUT_OF_BOUNDS")
            # Exact dialogue evidence uses supplied cue text, never a model-created quotation.
            text = " / ".join(c["text"] for c in cues) if trusted else evidence.text
            if len(text.encode()) > 15000:
                raise RagError("CHUNK_TOKEN_BUDGET_EXCEEDED")
            docs.append({"chunkId": digest([job["build_id"], parent, evidence_index]), "parentId": parent,
                         "buildId": job["build_id"], "dramaId": snapshot.drama_id, "sourceVersion": snapshot.source_version,
                         "embeddingProfile": job["profile"], "episodeId": episode.episode_id, "episodeNumber": episode.episode_number,
                         "docType": "scene", "text": text, "parentSummary": scene.summary,
                         "evidenceType": evidence.kind, "startTime": a, "endTime": b, "canSeek": trusted,
                         "sourceRefs": evidence.source_refs, "timePrecision": "subtitle" if trusted else "segment" if a is not None else "unknown"})
            docs[-1]["parentContext"] = scene.model_dump(by_alias=True)
            docs[-1]["sourceEvidence"] = {r: refs[r] for r in evidence.source_refs}
    return docs


class Pipeline:
    def __init__(self, settings, store, models, index, content):
        self.settings, self.store, self.models, self.index, self.content = settings, store, models, index, content

    def run(self, job, guard):
        if job["pipeline_version"] != self.settings.pipeline_version or job["profile"] != self.settings.profile:
            raise RagError("PIPELINE_PROFILE_MISMATCH", status=409)
        guard()
        raw = self.content.snapshot(job["snapshot_id"])
        snapshot = Snapshot.model_validate(raw)
        if (snapshot.drama_id, snapshot.source_version) != (job["drama_id"], job["source_version"]):
            raise Superseded()

        def stage(key, inputs, action):
            guard()
            ih = digest([snapshot.source_hash, job["pipeline_version"], inputs])
            cached = self.store.cached(job, key, ih)
            if cached is not None:
                return cached
            self.store.state(job, "RUNNING", key)
            result = action()
            guard()
            self.store.save_stage(job, key, ih, result)
            return result

        docs = []
        for episode in snapshot.episodes:
            directory = self.settings.data_dir / job["build_id"] / episode.episode_id
            video = directory / "source.bin"
            media_hash = download(episode.media, video, self.settings)
            # Hash is persisted on first download and compared on every recovery.
            stored = stage(episode.episode_id + ":identity", episode.media.identity, lambda: {"sha256": media_hash})
            if stored["sha256"] != media_hash:
                raise RagError("MEDIA_CHANGED_DURING_BUILD")
            duration, has_audio = probe(video, self.settings.max_duration)
            episode.duration = duration
            cues = []
            if episode.subtitle:
                subtitle_file = directory / "subtitle.txt"
                subtitle_hash = download(episode.subtitle, subtitle_file, self.settings)
                cues = stage(episode.episode_id + ":subtitle", subtitle_hash, lambda: subtitles(subtitle_file, episode.episode_id, duration))
            offset = 0.0
            while offset < duration:
                length = min(self.settings.segment_seconds, duration - offset)
                clip, audio = segment(video, directory / "segments", offset, length, has_audio)
                key = f"{episode.episode_id}:{offset:.3f}"
                visual = stage(key + ":video", media_hash, lambda: self.models.video(clip))
                observation = {"id": "video:" + key, "text": visual["text"], "startTime": offset, "endTime": offset + length}
                speech = [c for c in cues if c["startTime"] < offset + length and c["endTime"] > offset]
                if not speech and audio:
                    transcribed = stage(key + ":audio", media_hash, lambda: self.models.audio(audio))
                    speech = [{"id": "audio:" + key, "text": transcribed["text"]}]
                background = {"id": "background:" + snapshot.snapshot_id, "text": json.dumps({"title":snapshot.title,"description":snapshot.description,"storyLine":snapshot.story_line,
                    "actors":[{"name":a.get("name"),"role":a.get("role")} for a in snapshot.actors],"tags":snapshot.tags,"categories":snapshot.categories},ensure_ascii=False)}
                fused = stage(key + ":fuse", [observation, speech, background],
                              lambda: self.models.fuse([observation], speech, [background], SceneOutput.model_json_schema(by_alias=True)))
                try:
                    scenes = SceneOutput.model_validate(fused["parsed"])
                except ValueError:
                    raise RagError("SCENE_SCHEMA_INVALID") from None
                refs = {r["id"]: r for r in [observation, background, *speech]}
                docs.extend(build_chunks(snapshot, job, episode, offset, length, scenes, refs))
                offset += length
        # Subtitle cues may occur in two segments. Coarse observations at distinct times remain distinct.
        unique = {}
        for d in docs:
            unique.setdefault((d["episodeId"], d["evidenceType"], d["text"], d["startTime"]), d)
        docs = list(unique.values())
        if {d["episodeId"] for d in docs} != {e.episode_id for e in snapshot.episodes}:
            raise RagError("EPISODE_EVIDENCE_INCOMPLETE")
        summary = stage("drama-summary", [d["parentSummary"] for d in docs], lambda: self.models.chat(self.settings.text_model,
                        "仅根据以下资料生成简洁整剧摘要，不编造事实、台词或时间。人物和角色仅按给定资料引用，不推测与画面人物的对应。\n" + snapshot.title + "\n" + snapshot.description + "\n" + snapshot.story_line + "\n人物/题材：" + json.dumps({"actors":[{"name":a.get("name"),"role":a.get("role")} for a in snapshot.actors],"tags":snapshot.tags,"categories":snapshot.categories},ensure_ascii=False) + "\n" + "\n".join(dict.fromkeys(d["parentSummary"] for d in docs))[:16000]))
        sid = digest([job["build_id"], "summary"])
        docs.append({"chunkId": sid, "parentId": sid, "buildId": job["build_id"], "dramaId": snapshot.drama_id,
                     "sourceVersion": snapshot.source_version, "embeddingProfile": job["profile"], "episodeId": "", "episodeNumber": 0,
                     "docType": "summary", "text": summary["text"], "parentSummary": summary["text"], "evidenceType": "background",
                     "startTime": None, "endTime": None, "canSeek": False, "sourceRefs": sorted({"background:" + snapshot.snapshot_id,*[r for d in docs for r in d["sourceRefs"]]}), "timePrecision": "unknown"})
        vectors = []
        for start in range(0, len(docs), 10):
            texts = [d["text"] for d in docs[start:start + 10]]
            vectors.extend(stage(f"embedding:{start}", texts, lambda: self.models.embed(texts))["vectors"])
        self.store.save_documents(job, docs, "INDEXED")
        self.index.write(docs, vectors, guard)
        self.store.state(job, "INDEXED", "visibility-check")
        self.index.verify(docs, vectors)
        guard()
        manifest = {"documentCount": len(docs), "episodeIds": [e.episode_id for e in snapshot.episodes]}
        # Registration rejects stale/rejected snapshots. A READY build is still private until publication.
        self.content.register(job, manifest)
        guard()
        self.store.save_documents(job, docs, "READY")
        return manifest

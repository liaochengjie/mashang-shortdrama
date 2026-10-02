from __future__ import annotations
import json

from ai_service.schemas.rag import SearchRequest
from ai_service.models.errors import RagError
from ai_service.services.pipeline import digest


def candidate(doc):
    return {"dramaId": doc["dramaId"], "sourceVersion": doc["sourceVersion"], "buildId": doc["buildId"],
            "embeddingProfile": doc["embeddingProfile"], "episodeId": doc["episodeId"] or None}


class Search:
    def __init__(self, settings, store, models, index, content):
        self.settings, self.store, self.models, self.index, self.content = settings, store, models, index, content

    def validate(self, docs):
        if not docs:
            return []
        valid = self.content.validate([candidate(d) for d in docs])
        def key(i):
            return (i["dramaId"], i["sourceVersion"], i["buildId"], i["embeddingProfile"], i.get("episodeId") or None)
        by_key = {key(i): i for i in valid if i.get("valid")}
        results = []
        for d in docs:
            info = by_key.get(key(d))
            if info:
                results.append({**d, "display": info})
        return results

    def run(self, req: SearchRequest):
        query_hash = digest([req.query, self.settings.profile])
        if req.session_id:
            session = self.store.session(req.session_id, query_hash, req.page_size)
            docs, degraded = session["docs"], session["degraded"]
            sid = req.session_id
        else:
            degraded = []
            try:
                vector = self.models.embed([req.query])["vectors"][0]
            except RagError:
                vector = None
                degraded.append("BM25_ONLY")
            active = set(self.content.active_builds(self.settings.profile))
            builds = [b for b in self.store.ready_builds(self.settings.profile) if b in active]
            docs = []
            # Bounded replacement of invalid old versions.
            for limit in (30, 60, 120):
                ids = self.index.recall(req.query, vector, builds, limit)
                stored = self.store.documents(ids)
                docs = self.validate([stored[i] for i in ids if i in stored])
                if len({d["dramaId"] for d in docs}) >= req.page_size or len(ids) < limit:
                    break
            # Preserve budget for other scenes and dramas before paying for reranking.
            parents, dramas, shortlisted = {}, {}, []
            for doc in docs:
                parent=(doc["dramaId"],doc["parentId"])
                if parents.get(parent,0)>=2 or dramas.get(doc["dramaId"],0)>=8:
                    continue
                parents[parent]=parents.get(parent,0)+1
                dramas[doc["dramaId"]]=dramas.get(doc["dramaId"],0)+1
                shortlisted.append(doc)
                if len(shortlisted)==40:break
            docs = shortlisted
            if docs:
                try:
                    ranks = self.models.rerank(req.query, [(d["text"][:600] + "\n场景：" + json.dumps(d.get("parentContext",d["parentSummary"]),ensure_ascii=False)[:2400])[:3000] for d in docs])
                    docs = [docs[r["index"]] for r in ranks if r["relevance_score"] >= self.settings.min_rerank_score]
                except RagError:
                    degraded.append("RERANK_UNAVAILABLE")
            by_drama = {}
            for d in docs:
                by_drama.setdefault(d["dramaId"], d)
            docs = list(by_drama.values())[:self.settings.candidate_window]
            for d in docs:
                d.pop("display", None)
            sid = self.store.save_session(query_hash, req.page_size, {"docs": docs, "degraded": degraded}, self.settings.session_ttl)
        start = (req.page - 1) * req.page_size
        if start >= self.settings.candidate_window:
            raise RagError("SEARCH_WINDOW_EXCEEDED", status=400)
        # Every page is revalidated; a cached session never bypasses an immediate takedown.
        current = self.validate(docs[start:start + req.page_size])
        items = []
        for d in current:
            info = d["display"]
            items.append({"id": d["dramaId"], "dramaId": d["dramaId"], "title": info["title"], "cover": info.get("cover", ""),
                          "actors": info.get("actors", []), "matchedEpisodeId": d["episodeId"] or None,
                          "episodeNumber": d["episodeNumber"] or None, "matchText": d["text"], "parentSummary": d["parentSummary"],
                          "evidenceType": d["evidenceType"], "startTime": d["startTime"], "endTime": d["endTime"], "canSeek": d["canSeek"],
                          "sourceVersion": d["sourceVersion"], "buildId": d["buildId"], "embeddingProfile": d["embeddingProfile"]})
        return {"list": items, "sessionId": sid, "hasMore": start + req.page_size < len(docs),
                "windowSize": len(docs), "ttlSeconds": self.settings.session_ttl, "degraded": degraded}

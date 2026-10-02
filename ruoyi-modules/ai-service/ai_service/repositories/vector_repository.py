from __future__ import annotations

import json
from ai_service.models.errors import RagError


def rrf(rankings, k=60):
    scores = {}
    for ranking in rankings:
        seen = set()
        for position, chunk in enumerate(ranking, 1):
            if chunk in seen:
                continue
            seen.add(chunk)
            scores[chunk] = scores.get(chunk, 0) + 1 / (k + position)
    return sorted(scores, key=lambda x: (-scores[x], x))


class MilvusIndex:
    def __init__(self, settings):
        from pymilvus import MilvusClient
        self.settings = settings
        self.client = MilvusClient(uri=settings.milvus_uri, token=settings.milvus_token, timeout=15)
        self.collection = "kcat_" + settings.profile

    def health(self):
        if not self.client.has_collection(self.collection):
            raise RagError("COLLECTION_MISSING")

    def initialize(self):
        from pymilvus import DataType, Function, FunctionType
        if self.client.has_collection(self.collection):
            detail = self.client.describe_collection(self.collection)
            dense = next(f for f in detail["fields"] if f["name"] == "dense")
            if int(dense["params"]["dim"]) != self.settings.dimensions:
                raise RagError("COLLECTION_PROFILE_MISMATCH")
            return
        schema = self.client.create_schema(auto_id=False, enable_dynamic_field=False)
        schema.add_field("chunkId", DataType.VARCHAR, is_primary=True, max_length=64)
        for name in ("buildId", "dramaId", "episodeId", "parentId", "docType", "evidenceType"):
            schema.add_field(name, DataType.VARCHAR, max_length=80)
        schema.add_field("sourceVersion", DataType.INT64)
        schema.add_field("episodeNumber", DataType.INT64)
        schema.add_field("canSeek", DataType.BOOL)
        schema.add_field("startTime", DataType.DOUBLE, nullable=True)
        schema.add_field("endTime", DataType.DOUBLE, nullable=True)
        schema.add_field("text", DataType.VARCHAR, max_length=16000, enable_analyzer=True, enable_match=True,
                         analyzer_params={"type": "chinese"})
        schema.add_field("dense", DataType.FLOAT_VECTOR, dim=self.settings.dimensions)
        schema.add_field("sparse", DataType.SPARSE_FLOAT_VECTOR)
        schema.add_function(Function(name="bm25", input_field_names=["text"], output_field_names=["sparse"], function_type=FunctionType.BM25))
        params = self.client.prepare_index_params()
        params.add_index("dense", index_type="AUTOINDEX", metric_type="COSINE")
        params.add_index("sparse", index_type="SPARSE_INVERTED_INDEX", metric_type="BM25", params={"inverted_index_algo": "DAAT_MAXSCORE"})
        self.client.create_collection(self.collection, schema=schema, index_params=params, consistency_level="Strong")

    def write(self, docs, vectors, guard):
        for start in range(0, len(docs), 50):
            guard()
            rows = []
            for d, v in zip(docs[start:start + 50], vectors[start:start + 50], strict=True):
                rows.append({k: d[k] for k in ("chunkId", "buildId", "dramaId", "episodeId", "parentId", "docType", "evidenceType", "sourceVersion", "episodeNumber", "canSeek", "startTime", "endTime", "text")} | {"dense": v})
            self.client.upsert(self.collection, rows)
        guard()

    def verify(self, docs, vectors):
        ids = [d["chunkId"] for d in docs]
        for start in range(0, len(ids), 100):
            rows = self.client.query(self.collection, filter="chunkId in " + json.dumps(ids[start:start + 100]),
                                     output_fields=["chunkId"], consistency_level="Strong", limit=100)
            if {r["chunkId"] for r in rows} != set(ids[start:start + 100]):
                raise RagError("INDEX_NOT_VISIBLE", True)
        # A real dense AND sparse query for every episode plus the drama summary.
        samples = {}
        for d, vector in zip(docs, vectors, strict=True):
            samples.setdefault(d["episodeId"], (d, vector))
        for d, vector in samples.values():
            expression = "buildId == " + json.dumps(d["buildId"]) + " and episodeId == " + json.dumps(d["episodeId"])
            for field, value, metric in (("dense", vector, "COSINE"), ("sparse", d["text"], "BM25")):
                result = self.client.search(self.collection, data=[value], anns_field=field, filter=expression,
                                            search_params={"metric_type": metric}, limit=5, consistency_level="Strong")
                if not result[0]:
                    raise RagError("INDEX_QUERY_NOT_READY", True)

    def recall(self, query, vector, build_ids, limit=60):
        if not build_ids:
            return []
        # Bound expressions; no injection from user queries or ids.
        expression = "buildId in " + json.dumps(build_ids)
        rankings = []
        for doc_type, quota in (("scene", limit), ("summary", max(5, limit // 5))):
            filt = expression + " and docType == " + json.dumps(doc_type)
            for field, value, metric in (("dense", vector, "COSINE"), ("sparse", query, "BM25")):
                if value is None:
                    continue
                rows = self.client.search(self.collection, data=[value], anns_field=field, filter=filt,
                                          limit=quota, search_params={"metric_type": metric}, consistency_level="Strong")
                rankings.append([r["id"] for r in rows[0] if metric != "BM25" or r["distance"] > 0])
        return rrf(rankings)

    def delete_build(self, build_id):
        self.client.delete(self.collection, filter="buildId == " + json.dumps(build_id))

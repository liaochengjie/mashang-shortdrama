from __future__ import annotations

import json
import sqlite3
import time
import uuid
from contextlib import contextmanager
from pathlib import Path
from urllib.parse import urlparse, unquote

from ai_service.schemas.rag import JobRequest
from ai_service.models.errors import LostLease, RagError


SCHEMA = [
    "CREATE TABLE IF NOT EXISTS rag_external_binding (job_id VARCHAR(36) PRIMARY KEY, task_id VARCHAR(64) NOT NULL UNIQUE)",
    "CREATE TABLE IF NOT EXISTS rag_parent (build_id VARCHAR(36) NOT NULL, parent_id VARCHAR(64) NOT NULL, context_json LONGTEXT NOT NULL, PRIMARY KEY(build_id,parent_id))",
    "CREATE TABLE IF NOT EXISTS rag_job (job_id VARCHAR(36) PRIMARY KEY, snapshot_id VARCHAR(36) NOT NULL, drama_id VARCHAR(32) NOT NULL, source_version BIGINT NOT NULL, pipeline_version VARCHAR(80) NOT NULL, profile VARCHAR(80) NOT NULL, build_id VARCHAR(36) NOT NULL, state VARCHAR(32) NOT NULL, owner VARCHAR(80), lease_until DOUBLE NOT NULL DEFAULT 0, fence BIGINT NOT NULL DEFAULT 0, attempts INT NOT NULL DEFAULT 0, next_attempt DOUBLE NOT NULL DEFAULT 0, error VARCHAR(80), progress VARCHAR(100), UNIQUE(drama_id, source_version, pipeline_version, profile))",
    "CREATE TABLE IF NOT EXISTS rag_stage (job_id VARCHAR(36) NOT NULL, stage_key VARCHAR(200) NOT NULL, input_hash VARCHAR(64) NOT NULL, result_json LONGTEXT NOT NULL, PRIMARY KEY(job_id, stage_key))",
    "CREATE TABLE IF NOT EXISTS rag_document (chunk_id VARCHAR(64) PRIMARY KEY, build_id VARCHAR(36) NOT NULL, parent_id VARCHAR(64) NOT NULL, document_json LONGTEXT NOT NULL)",
    "CREATE TABLE IF NOT EXISTS rag_build (build_id VARCHAR(36) PRIMARY KEY, job_id VARCHAR(36) NOT NULL, profile VARCHAR(80) NOT NULL, state VARCHAR(32) NOT NULL, manifest_json LONGTEXT NOT NULL)",
    "CREATE TABLE IF NOT EXISTS rag_session (session_id VARCHAR(36) PRIMARY KEY, query_hash VARCHAR(64) NOT NULL, page_size INT NOT NULL, expires DOUBLE NOT NULL, result_json LONGTEXT NOT NULL)",
]


class Connection:
    def __init__(self, raw, mysql):
        self.raw, self.mysql = raw, mysql

    def execute(self, sql, args=()):
        if not self.mysql:
            return self.raw.execute(sql, args)
        cursor = self.raw.cursor()
        cursor.execute(sql.replace("?", "%s"), args)
        return cursor


class Store:
    """SQLite for offline tests; MySQL is a separate logical database in deployment."""
    def __init__(self, url):
        self.url = url
        self.mysql = url.startswith("mysql://")
        if not self.mysql and not url.startswith("sqlite:///"):
            raise ValueError("database URL must be mysql:// or sqlite:///")

    @contextmanager
    def tx(self):
        if self.mysql:
            import pymysql
            u = urlparse(self.url)
            raw = pymysql.connect(host=u.hostname, port=u.port or 3306, user=unquote(u.username or ""),
                                  password=unquote(u.password or ""), database=u.path.lstrip("/"),
                                  charset="utf8mb4", cursorclass=pymysql.cursors.DictCursor,
                                  connect_timeout=5, read_timeout=10, write_timeout=10)
        else:
            path = self.url.removeprefix("sqlite:///")
            Path(path).parent.mkdir(parents=True, exist_ok=True)
            raw = sqlite3.connect(path, timeout=10)
            raw.row_factory = sqlite3.Row
            raw.execute("PRAGMA journal_mode=WAL")
            raw.execute("BEGIN IMMEDIATE")
        try:
            yield Connection(raw, self.mysql)
            raw.commit()
        except Exception:
            raw.rollback()
            raise
        finally:
            raw.close()

    def initialize(self):
        with self.tx() as c:
            for sql in SCHEMA:
                c.execute(sql + (" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4" if self.mysql else ""))

    def health(self):
        with self.tx() as c:
            c.execute("SELECT job_id FROM rag_job LIMIT 1").fetchall()

    def accept(self, req: JobRequest):
        with self.tx() as c:
            prefix = "INSERT IGNORE" if self.mysql else "INSERT OR IGNORE"
            c.execute(prefix + " INTO rag_job(job_id,snapshot_id,drama_id,source_version,pipeline_version,profile,build_id,state) VALUES(?,?,?,?,?,?,?,?)",
                      (str(uuid.uuid4()), req.snapshot_id, req.drama_id, req.source_version, req.pipeline_version,
                       req.embedding_profile, str(uuid.uuid4()), "QUEUED"))
            row = c.execute("SELECT * FROM rag_job WHERE drama_id=? AND source_version=? AND pipeline_version=? AND profile=?",
                            (req.drama_id, req.source_version, req.pipeline_version, req.embedding_profile)).fetchone()
            if row["snapshot_id"] != req.snapshot_id:
                raise RagError("IDEMPOTENCY_CONFLICT", status=409)
            return dict(row)

    def get(self, job_id):
        with self.tx() as c:
            r = c.execute("SELECT * FROM rag_job WHERE job_id=?", (job_id,)).fetchone()
            if not r:
                raise RagError("JOB_NOT_FOUND", status=404)
            return dict(r)

    def claim(self, owner, seconds, job_id=None):
        now = time.time()
        with self.tx() as c:
            states = "'QUEUED','RUNNING','INDEXED','RETRYABLE_FAILED'" + (",'READY'" if job_id else "")
            sql = f"SELECT job_id FROM rag_job WHERE state IN ({states}) AND lease_until<? AND next_attempt<=?"
            args = [now, now]
            if job_id:
                sql += " AND job_id=?"
                args.append(job_id)
            else:
                sql += " AND NOT EXISTS (SELECT 1 FROM rag_external_binding x WHERE x.job_id=rag_job.job_id)"
            sql += " ORDER BY source_version LIMIT 1"
            r = c.execute(sql, tuple(args)).fetchone()
            if not r:
                return None
            changed = c.execute("UPDATE rag_job SET owner=?,lease_until=?,fence=fence+1,attempts=attempts+1,state='RUNNING' WHERE job_id=? AND lease_until<?",
                                (owner, now + seconds, r["job_id"], now)).rowcount
            if changed != 1:
                return None
            return dict(c.execute("SELECT * FROM rag_job WHERE job_id=?", (r["job_id"],)).fetchone())

    def guard(self, c, job):
        row = c.execute("SELECT owner,fence,lease_until FROM rag_job WHERE job_id=?", (job["job_id"],)).fetchone()
        if not row or row["owner"] != job["owner"] or row["fence"] != job["fence"] or row["lease_until"] <= time.time():
            raise LostLease()
        # Lock the row on MySQL so a competing claimant cannot fence a stage commit.
        if self.mysql:
            row = c.execute("SELECT owner,fence,lease_until FROM rag_job WHERE job_id=? FOR UPDATE", (job["job_id"],)).fetchone()
            if row["owner"] != job["owner"] or row["fence"] != job["fence"] or row["lease_until"] <= time.time():
                raise LostLease()

    def renew(self, job, seconds):
        with self.tx() as c:
            changed = c.execute("UPDATE rag_job SET lease_until=? WHERE job_id=? AND owner=? AND fence=? AND lease_until>?",
                                (time.time() + seconds, job["job_id"], job["owner"], job["fence"], time.time())).rowcount
            if changed != 1:
                raise LostLease()

    def state(self, job, state, progress="", error=None, delay=0):
        with self.tx() as c:
            self.guard(c, job)
            terminal = state in ("READY", "FAILED", "SUPERSEDED", "RETRYABLE_FAILED")
            c.execute("UPDATE rag_job SET state=?,progress=?,error=?,next_attempt=?,lease_until=? WHERE job_id=? AND owner=? AND fence=?",
                      (state, progress, error, time.time() + delay, 0 if terminal else time.time() + 120,
                       job["job_id"], job["owner"], job["fence"]))

    def cached(self, job, key, input_hash):
        with self.tx() as c:
            self.guard(c, job)
            row = c.execute("SELECT result_json FROM rag_stage WHERE job_id=? AND stage_key=? AND input_hash=?",
                            (job["job_id"], key, input_hash)).fetchone()
            return json.loads(row["result_json"]) if row else None

    def save_stage(self, job, key, input_hash, result):
        with self.tx() as c:
            self.guard(c, job)
            c.execute("DELETE FROM rag_stage WHERE job_id=? AND stage_key=?", (job["job_id"], key))
            c.execute("INSERT INTO rag_stage VALUES(?,?,?,?)", (job["job_id"], key, input_hash, json.dumps(result, ensure_ascii=False)))

    def save_documents(self, job, docs, state):
        with self.tx() as c:
            self.guard(c, job)
            c.execute("DELETE FROM rag_document WHERE build_id=?", (job["build_id"],))
            c.execute("DELETE FROM rag_parent WHERE build_id=?", (job["build_id"],))
            parents = {}
            for d in docs:
                c.execute("INSERT INTO rag_document VALUES(?,?,?,?)", (d["chunkId"], job["build_id"], d["parentId"], json.dumps(d, ensure_ascii=False)))
                parents.setdefault(d["parentId"], d.get("parentContext", {"summary": d["parentSummary"]}))
            for parent_id, context in parents.items():
                c.execute("INSERT INTO rag_parent VALUES(?,?,?)", (job["build_id"], parent_id, json.dumps(context, ensure_ascii=False)))
            c.execute("DELETE FROM rag_build WHERE build_id=?", (job["build_id"],))
            manifest = {"documentCount": len(docs), "episodeIds": sorted({d["episodeId"] for d in docs if d["episodeId"]})}
            c.execute("INSERT INTO rag_build VALUES(?,?,?,?,?)", (job["build_id"], job["job_id"], job["profile"], state, json.dumps(manifest)))

    def documents(self, chunk_ids):
        if not chunk_ids:
            return {}
        with self.tx() as c:
            rows = c.execute("SELECT * FROM rag_document WHERE chunk_id IN (" + ",".join("?" for _ in chunk_ids) + ")", tuple(chunk_ids)).fetchall()
            return {r["chunk_id"]: json.loads(r["document_json"]) for r in rows}

    def ready_builds(self, profile):
        with self.tx() as c:
            return [r["build_id"] for r in c.execute("SELECT build_id FROM rag_build WHERE profile=? AND state='READY'", (profile,)).fetchall()]

    def bind_external(self, job_id, task_id):
        with self.tx() as c:
            prefix = "INSERT IGNORE" if self.mysql else "INSERT OR IGNORE"
            c.execute(prefix + " INTO rag_external_binding VALUES(?,?)", (job_id, task_id))
            row = c.execute("SELECT task_id FROM rag_external_binding WHERE job_id=?", (job_id,)).fetchone()
            if row["task_id"] != task_id:
                raise RagError("EXTERNAL_TASK_BINDING_CONFLICT", status=409)

    def retry(self, job_id):
        with self.tx() as c:
            return c.execute("UPDATE rag_job SET state='QUEUED',attempts=0,error=NULL,next_attempt=0 WHERE job_id=? AND state='FAILED' AND lease_until<?", (job_id, time.time())).rowcount

    def builds(self, profile):
        with self.tx() as c:
            return [dict(r) for r in c.execute("SELECT b.*,j.snapshot_id,j.drama_id,j.source_version FROM rag_build b JOIN rag_job j ON j.job_id=b.job_id WHERE b.profile=?", (profile,)).fetchall()]

    def retire(self, build_id):
        with self.tx() as c:
            # An active worker cannot have its documents removed underneath it.
            if c.execute("SELECT job_id FROM rag_job WHERE build_id=? AND lease_until>?", (build_id, time.time())).fetchone():
                raise LostLease()
            c.execute("UPDATE rag_build SET state='RETIRED' WHERE build_id=?", (build_id,))
            c.execute("UPDATE rag_job SET state='SUPERSEDED',fence=fence+1,lease_until=0 WHERE build_id=?",(build_id,))
            c.execute("DELETE FROM rag_document WHERE build_id=?", (build_id,))
            c.execute("DELETE FROM rag_parent WHERE build_id=?", (build_id,))

    def usage(self, build_id):
        with self.tx() as c:
            rows=c.execute("SELECT s.stage_key,s.result_json FROM rag_stage s JOIN rag_job j ON j.job_id=s.job_id WHERE j.build_id=?",(build_id,)).fetchall()
            result=[]
            for row in rows:
                data=json.loads(row["result_json"])
                if isinstance(data,dict) and "model" in data:
                    result.append({"stage":row["stage_key"],"model":data["model"],"requestId":data.get("requestId"),"usage":data.get("usage")})
            return result

    def session(self, session_id, query_hash, page_size):
        with self.tx() as c:
            r = c.execute("SELECT * FROM rag_session WHERE session_id=?", (session_id,)).fetchone()
            if not r or r["expires"] < time.time():
                raise RagError("SEARCH_SESSION_EXPIRED", status=410)
            if r["query_hash"] != query_hash or r["page_size"] != page_size:
                raise RagError("SEARCH_SESSION_MISMATCH", status=409)
            return json.loads(r["result_json"])

    def save_session(self, query_hash, page_size, result, ttl):
        sid = str(uuid.uuid4())
        with self.tx() as c:
            c.execute("DELETE FROM rag_session WHERE expires<?", (time.time(),))
            c.execute("INSERT INTO rag_session VALUES(?,?,?,?,?)", (sid, query_hash, page_size, time.time() + ttl, json.dumps(result, ensure_ascii=False)))
        return sid

-- MySQL 8.0, run explicitly in the LOCAL development content database.
-- No DELETE/TRUNCATE, no implicit legacy publication or process migration.
CREATE TABLE kcat_rag_release (
  drama_id BIGINT NOT NULL PRIMARY KEY,
  source_version BIGINT NOT NULL DEFAULT 0,
  published_source_version BIGINT NULL,
  published_snapshot_id VARCHAR(36) NULL,
  published_build_id VARCHAR(36) NULL,
  embedding_profile VARCHAR(80) NULL,
  desired_on_shelf TINYINT NOT NULL DEFAULT 1,
  deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE kcat_rag_snapshot (
  snapshot_id VARCHAR(36) NOT NULL PRIMARY KEY,
  drama_id BIGINT NOT NULL,
  source_version BIGINT NOT NULL,
  source_hash CHAR(64) NOT NULL,
  snapshot_json LONGTEXT NOT NULL,
  audit_state VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  media_state VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  index_state VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
  build_id VARCHAR(36) NULL,
  profile VARCHAR(80) NOT NULL,
  pipeline_version VARCHAR(80) NOT NULL,
  process_id VARCHAR(64) NULL,
  UNIQUE KEY uk_snapshot_version (drama_id, source_version)
) ENGINE=InnoDB;

CREATE TABLE kcat_rag_outbox (
  event_id VARCHAR(36) NOT NULL PRIMARY KEY,
  event_type VARCHAR(20) NOT NULL,
  snapshot_id VARCHAR(36) NOT NULL,
  payload_json LONGTEXT NOT NULL,
  event_state VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  attempts INT NOT NULL DEFAULT 0,
  owner VARCHAR(36) NULL,
  lease_until DOUBLE NOT NULL DEFAULT 0,
  fence BIGINT NOT NULL DEFAULT 0,
  next_attempt DOUBLE NOT NULL DEFAULT 0,
  error_code VARCHAR(80) NULL,
  UNIQUE KEY uk_snapshot_event (snapshot_id,event_type),
  KEY ix_outbox_poll (event_state,next_attempt,lease_until)
) ENGINE=InnoDB;

CREATE TABLE kcat_rag_media (
  snapshot_id VARCHAR(36) NOT NULL,
  episode_id BIGINT NOT NULL,
  media_identity VARCHAR(512) NOT NULL,
  state VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  file_id VARCHAR(100) NULL,
  task_id VARCHAR(200) NULL,
  task_started_at DOUBLE NOT NULL DEFAULT 0,
  output_url TEXT NULL,
  attempts INT NOT NULL DEFAULT 0,
  error_code VARCHAR(80) NULL,
  owner VARCHAR(36) NULL,
  lease_until DOUBLE NOT NULL DEFAULT 0,
  fence BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY(snapshot_id,episode_id)
) ENGINE=InnoDB;

CREATE TABLE kcat_rag_asset (
  url_hash CHAR(64) NOT NULL PRIMARY KEY,
  identity VARCHAR(512) NOT NULL,
  sha256 CHAR(64) NOT NULL,
  size_bytes BIGINT NOT NULL
) ENGINE=InnoDB;

-- ROLLBACK: disable rag.enabled, switch consumers to legacy interfaces; export these
-- tables first. Drop ONLY the five kcat_rag_* tables after stopping new workers.
-- Do not reset content tables, Camunda history, storage volumes or running instances.

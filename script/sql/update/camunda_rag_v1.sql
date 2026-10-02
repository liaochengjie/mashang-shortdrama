-- Execute separately in the LOCAL camunda-service engine database.
CREATE TABLE kcat_rag_process_start (
 business_key VARCHAR(120) NOT NULL PRIMARY KEY,
 snapshot_id VARCHAR(36) NOT NULL UNIQUE,
 process_id VARCHAR(64) NULL,
 decision_state VARCHAR(20) NULL
) ENGINE=InnoDB;
-- Rollback: keep rows while corresponding process instances remain; do not delete
-- Camunda runtime/history tables. Export and drop this table only after retirement.

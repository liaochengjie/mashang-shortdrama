package com.lfy.kcat.content.job;

import com.lfy.kcat.content.biz.RagReleaseService;
import com.lfy.kcat.content.config.RagProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author liaochengjie
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RagOutboxDispatcher {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;
    private final RagProperties properties;
    private final RagReleaseService release;
    private final com.lfy.kcat.content.service.DramaAuthService dramaAuthService;
    private final String owner = UUID.randomUUID().toString();

    @Scheduled(fixedDelay = 3000)
    public void dispatch() {
        if (!properties.isEnabled()) return;
        try {
            double now = System.currentTimeMillis()/1000.0;
            List<Map<String,Object>> candidates = jdbc.queryForList("SELECT * FROM kcat_rag_outbox WHERE event_state IN ('PENDING','RUNNING') AND lease_until<? AND next_attempt<=? ORDER BY next_attempt LIMIT 5", now, now);
            for (Map<String,Object> event : candidates) {
                String id = event.get("event_id").toString();
                if (jdbc.update("UPDATE kcat_rag_outbox SET owner=?,lease_until=?,fence=fence+1,attempts=attempts+1,event_state='RUNNING' WHERE event_id=? AND lease_until<? AND event_state IN ('PENDING','RUNNING')", owner, now+60, id, now) != 1) continue;
                Map<String,Object> claimed = jdbc.queryForMap("SELECT * FROM kcat_rag_outbox WHERE event_id=?", id);
                Object fence = claimed.get("fence");
                try {
                    Map<String,Object> body = release.decode(claimed.get("payload_json").toString());
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(5000); factory.setReadTimeout(15000);
                    RestClient client = RestClient.builder().baseUrl(properties.getCamundaUrl()).requestFactory(factory)
                        .defaultHeader("Authorization", "Bearer " + properties.getCamundaToken()).build();
                    boolean start = "START".equals(claimed.get("event_type"));
                    Map result = client.post().uri(start ? "/internal/rag/processes" : "/internal/rag/decisions")
                        .body(body).retrieve().body(Map.class);
                    transaction.executeWithoutResult(tx -> {
                        int updated = jdbc.update("UPDATE kcat_rag_outbox SET event_state='DONE',lease_until=0,error_code=NULL WHERE event_id=? AND owner=? AND fence=? AND lease_until>?", id, owner, fence, System.currentTimeMillis()/1000.0);
                        if (updated != 1) throw new IllegalStateException("OUTBOX_LEASE_LOST");
                        if (start) {
                            String processId = result.get("processId").toString();
                            jdbc.update("UPDATE kcat_rag_snapshot SET process_id=? WHERE snapshot_id=?", processId, claimed.get("snapshot_id"));
                            Map<String,Object> snapshot = release.decode(jdbc.queryForObject("SELECT snapshot_json FROM kcat_rag_snapshot WHERE snapshot_id=?", String.class, claimed.get("snapshot_id")));
                            var auth = new com.lfy.kcat.content.domain.DramaAuth();
                            auth.setDramaId(Long.valueOf(snapshot.get("dramaId").toString())); auth.setProcessId(processId); auth.setAuthStatus(0);
                            dramaAuthService.save(auth);
                        }
                    });
                } catch (Exception e) {
                    int attempts = ((Number)claimed.get("attempts")).intValue();
                    jdbc.update("UPDATE kcat_rag_outbox SET event_state=?,lease_until=0,next_attempt=?,error_code=? WHERE event_id=? AND owner=? AND fence=?",
                        attempts >= 8 ? "FAILED" : "PENDING", now + Math.min(300, Math.pow(2, attempts)), "REMOTE_" + e.getClass().getSimpleName(), id, owner, fence);
                    log.warn("RAG outbox event={} attempt={} failed ({})", id, attempts, e.getClass().getSimpleName());
                }
            }
        } catch (Exception e) { log.warn("RAG outbox dependency unavailable ({})", e.getClass().getSimpleName()); }
    }
}

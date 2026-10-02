package com.lfy.kcat.content.biz.impl;

import com.lfy.kcat.content.biz.RagMediaService;
import com.lfy.kcat.content.biz.RagReleaseService;
import com.lfy.kcat.content.config.RagProperties;

import com.lfy.kcat.content.vod.properties.VodProperties;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.VodConstant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liaochengjie
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagMediaServiceImpl implements RagMediaService {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;
    private final RagReleaseService release;
    private final RagProperties properties;
    private final VodProperties vodProperties;
    private final VodClient vod;
    private final VodUploadClient upload;
    private final String owner = UUID.randomUUID().toString();

    @Transactional
    public Map<String,Object> start(String id) {
        Map<String,Object> s = release.current(id,true);
        if (!"APPROVED".equals(s.get("audit_state"))) throw new IllegalStateException("APPROVAL_REQUIRED");
        jdbc.update("UPDATE kcat_rag_snapshot SET media_state='PROCESSING' WHERE snapshot_id=? AND media_state='PENDING'", id);
        return status(id);
    }

    public Map<String,Object> status(String id) {
        Map<String,Object> s = release.current(id,false);
        return Map.of("state", s.get("media_state"));
    }

    @Transactional
    public Map<String,Object> retry(String id) {
        Map<String,Object> s=release.current(id,true);
        if (!"APPROVED".equals(s.get("audit_state"))) throw new IllegalStateException("APPROVAL_REQUIRED");
        jdbc.update("UPDATE kcat_rag_media SET state='PENDING',attempts=0,error_code=NULL,task_id=NULL,task_started_at=0 WHERE snapshot_id=? AND state='FAILED' AND lease_until<?",id,now());
        jdbc.update("UPDATE kcat_rag_snapshot SET media_state='PROCESSING' WHERE snapshot_id=? AND media_state='FAILED'",id);
        return status(id);
    }

    @Scheduled(fixedDelay = 10000)
    public void poll() {
        if (!properties.isEnabled()) return;
        try {
            List<Map<String,Object>> rows = jdbc.queryForList("SELECT m.* FROM kcat_rag_media m JOIN kcat_rag_snapshot s ON s.snapshot_id=m.snapshot_id JOIN kcat_rag_release r ON r.drama_id=s.drama_id WHERE s.media_state='PROCESSING' AND s.audit_state='APPROVED' AND r.source_version=s.source_version AND r.deleted=0 AND m.state IN ('PENDING','PROCESSING') AND m.lease_until<? LIMIT 2", now());
            for (Map<String,Object> row : rows) process(row);
        } catch (Exception e) { log.warn("RAG media dependency unavailable ({})", e.getClass().getSimpleName()); }
    }

    private double now() { return System.currentTimeMillis()/1000.0; }

    private void process(Map<String,Object> input) {
        String id = input.get("snapshot_id").toString();
        Object episodeId = input.get("episode_id");
        if (jdbc.update("UPDATE kcat_rag_media SET owner=?,lease_until=?,fence=fence+1 WHERE snapshot_id=? AND episode_id=? AND lease_until<?", owner, now()+90, id, episodeId, now()) != 1) return;
        Map<String,Object> row = jdbc.queryForMap("SELECT * FROM kcat_rag_media WHERE snapshot_id=? AND episode_id=?", id, episodeId);
        Object fence = row.get("fence");
        AtomicBoolean lost = new AtomicBoolean(false);
        var heartbeat = Executors.newSingleThreadScheduledExecutor();
        heartbeat.scheduleAtFixedRate(() -> {
            try {
                if (jdbc.update("UPDATE kcat_rag_media SET lease_until=? WHERE snapshot_id=? AND episode_id=? AND owner=? AND fence=? AND lease_until>?", now()+90,id,episodeId,owner,fence,now()) != 1) lost.set(true);
            } catch (Exception e) { lost.set(true); }
        }, 20,20, TimeUnit.SECONDS);
        try {
            Map<String,Object> facts = release.snapshot(id);
            List<Map<String,Object>> eps = (List<Map<String,Object>>)facts.get("episodes");
            Map<String,Object> ep = eps.stream().filter(e -> e.get("episodeId").toString().equals(episodeId.toString())).findFirst().orElseThrow();
            Map<String,Object> media = (Map<String,Object>)ep.get("media");
            if (!row.get("media_identity").equals(media.get("identity"))) throw new IllegalStateException("MEDIA_VERSION_CONFLICT");
            String fileId = (String)row.get("file_id");
            if (fileId == null) {
                Path file = download(media);
                try {
                    VodUploadRequest req = new VodUploadRequest();
                    req.setMediaFilePath(file.toString()); req.setSubAppId(vodProperties.getStuAppId());
                    fileId = upload.upload(vodProperties.getRegion(), req).getFileId();
                    commit(row, lost, "UPDATE kcat_rag_media SET file_id=? WHERE snapshot_id=? AND episode_id=? AND owner=? AND fence=? AND lease_until>?", fileId,id,episodeId,owner,fence,now());
                } finally { Files.deleteIfExists(file); }
            }
            String taskId = (String)row.get("task_id");
            if (taskId == null) {
                ProcessMediaByProcedureRequest req = new ProcessMediaByProcedureRequest();
                req.setFileId(fileId); req.setProcedureName(VodConstant.QUALITY_FLOW_PROCESS); req.setSubAppId(vodProperties.getStuAppId());
                req.setSessionId("kcat:" + id + ":" + episodeId); req.setSessionContext(id);
                taskId = vod.ProcessMediaByProcedure(req).getTaskId();
                commit(row,lost,"UPDATE kcat_rag_media SET task_id=?,task_started_at=?,state='PROCESSING' WHERE snapshot_id=? AND episode_id=? AND owner=? AND fence=? AND lease_until>?", taskId,now(),id,episodeId,owner,fence,now());
            } else if (now()-((Number)row.get("task_started_at")).doubleValue()>14400) {
                throw new IllegalStateException("TRANSCODE_FOUR_HOUR_TIMEOUT");
            }
            DescribeTaskDetailRequest req = new DescribeTaskDetailRequest();
            req.setTaskId(taskId); req.setSubAppId(vodProperties.getStuAppId());
            DescribeTaskDetailResponse response = vod.DescribeTaskDetail(req);
            if ("FINISH".equals(response.getStatus())) {
                ProcedureTask task = response.getProcedureTask();
                if (task == null || task.getErrCode() == null || task.getErrCode() != 0 || !fileId.equals(task.getFileId()) || task.getMediaProcessResultSet() == null)
                    throw new IllegalStateException("TRANSCODE_FAILED");
                String output = null;
                for (MediaProcessTaskResult r : task.getMediaProcessResultSet()) {
                    MediaProcessTaskTranscodeResult t = r.getTranscodeTask();
                    if (t == null) continue;
                    if (!"SUCCESS".equals(t.getStatus()) || t.getErrCode() == null || t.getErrCode() != 0 || t.getOutput() == null)
                        throw new IllegalStateException("TRANSCODE_VARIANT_FAILED");
                    output = t.getOutput().getUrl();
                }
                if (output == null || output.isBlank()) throw new IllegalStateException("TRANSCODE_OUTPUT_MISSING");
                commit(row,lost,"UPDATE kcat_rag_media SET state='READY',output_url=?,error_code=NULL WHERE snapshot_id=? AND episode_id=? AND owner=? AND fence=? AND lease_until>?", output,id,episodeId,owner,fence,now());
            }
            transaction.executeWithoutResult(tx -> {
                release.current(id,true);
                Long pending = jdbc.queryForObject("SELECT COUNT(*) FROM kcat_rag_media WHERE snapshot_id=? AND state<>'READY'", Long.class,id);
                if (pending == 0) jdbc.update("UPDATE kcat_rag_snapshot SET media_state='READY' WHERE snapshot_id=?",id);
            });
        } catch (Exception e) {
            if (!lost.get()) {
                jdbc.update("UPDATE kcat_rag_media SET attempts=attempts+1,state=IF(attempts>=3,'FAILED',state),error_code=? WHERE snapshot_id=? AND episode_id=? AND owner=? AND fence=? AND lease_until>?", "MEDIA_"+e.getClass().getSimpleName(),id,episodeId,owner,fence,now());
                jdbc.update("UPDATE kcat_rag_snapshot SET media_state='FAILED' WHERE snapshot_id=? AND EXISTS (SELECT 1 FROM kcat_rag_media WHERE snapshot_id=? AND state='FAILED')",id,id);
            }
            log.warn("RAG media snapshot={} episode={} failed ({})",id,episodeId,e.getClass().getSimpleName());
        } finally {
            heartbeat.shutdownNow();
            jdbc.update("UPDATE kcat_rag_media SET lease_until=0 WHERE snapshot_id=? AND episode_id=? AND owner=? AND fence=?",id,episodeId,owner,fence);
        }
    }

    private void commit(Map<String,Object> row, AtomicBoolean lost, String sql, Object... args) {
        transaction.executeWithoutResult(tx -> {
            release.current(row.get("snapshot_id").toString(),true);
            if (lost.get() || jdbc.update(sql,args) != 1) throw new IllegalStateException("MEDIA_LEASE_LOST");
        });
    }

    private Path download(Map<String,Object> media) throws Exception {
        URI uri = URI.create(media.get("url").toString());
        if (!Set.of("http","https").contains(uri.getScheme()) || !Arrays.asList(properties.getMediaAllowedHosts().split(",")).contains(uri.getHost()) || uri.getUserInfo()!=null)
            throw new IllegalArgumentException("MEDIA_HOST_REJECTED");
        HttpURLConnection conn = (HttpURLConnection)uri.toURL().openConnection();
        conn.setInstanceFollowRedirects(false); conn.setConnectTimeout(5000); conn.setReadTimeout(15000);
        if (conn.getResponseCode()!=200) throw new IllegalStateException("MEDIA_DOWNLOAD_FAILED");
        Path path = Files.createTempFile("kcat-rag-", ".mp4");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in=conn.getInputStream(); var out=Files.newOutputStream(path)) {
            byte[] buffer = new byte[65536]; long size=0; int n;
            while ((n=in.read(buffer))!=-1) {
                size+=n; if (size>200_000_000) throw new IllegalArgumentException("MEDIA_TOO_LARGE");
                digest.update(buffer,0,n); out.write(buffer,0,n);
            }
            if (!HexFormat.of().formatHex(digest.digest()).equals(media.get("sha256"))) throw new IllegalStateException("MEDIA_CHECKSUM_MISMATCH");
            return path;
        } catch (Exception e) { Files.deleteIfExists(path); throw e; }
        finally { conn.disconnect(); }
    }
}

package com.lfy.kcat.content.biz.impl;

import com.lfy.kcat.content.biz.RagReleaseService;
import com.lfy.kcat.content.config.RagProperties;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lfy.kcat.content.domain.*;
import com.lfy.kcat.content.mapper.*;
import com.lfy.kcat.content.service.DramaTagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** Owns draft versions, immutable facts and the authoritative publication tuple.
 * @author liaochengjie
 */
@Service
@RequiredArgsConstructor
public class RagReleaseServiceImpl implements RagReleaseService {
    private final JdbcTemplate jdbc;
    private final ObjectMapper json;
    private final RagProperties properties;
    private final DramasMapper dramas;
    private final EpisodesMapper episodes;
    private final DramaActorsMapper actors;
    private final DramaTagsService tags;

    public boolean enabled() { return properties.isEnabled(); }

    @Override
    public Map<String,Object> health() {
        jdbc.queryForObject("SELECT COUNT(*) FROM kcat_rag_release", Long.class);
        return Map.of("status", "UP");
    }

    @Override
    public Map<String,Object> retryOutbox(String eventId) {
        int changed = jdbc.update("UPDATE kcat_rag_outbox SET event_state='PENDING',attempts=0,next_attempt=0 WHERE event_id=? AND event_state='FAILED' AND lease_until<?", eventId, System.currentTimeMillis()/1000.0);
        return Map.of("changed", changed);
    }

    public static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    public String encode(Object value) {
        try { return json.writeValueAsString(value); }
        catch (Exception e) { throw new IllegalArgumentException("RAG_JSON_INVALID"); }
    }

    public Map<String,Object> decode(String value) {
        try { return json.readValue(value, new TypeReference<Map<String,Object>>() {}); }
        catch (Exception e) { throw new IllegalStateException("RAG_STORED_JSON_INVALID"); }
    }

    private static ResponseStatusException conflict(String code) {
        return new ResponseStatusException(HttpStatus.CONFLICT, code);
    }

    @Transactional
    public String capture(Long dramaId) {
        return capture(dramaId, false);
    }

    @Transactional
    public String capture(Long dramaId, boolean force) {
        if (properties.getEmbeddingProfile().isBlank()) throw conflict("RAG_PROFILE_NOT_CONFIGURED");
        jdbc.update("INSERT IGNORE INTO kcat_rag_release(drama_id) VALUES(?)", dramaId);
        Map<String,Object> release = jdbc.queryForMap("SELECT * FROM kcat_rag_release WHERE drama_id=? FOR UPDATE", dramaId);
        Dramas drama = dramas.selectById(dramaId);
        if (drama == null) throw conflict("CONTENT_DELETED");
        if (((Number)release.get("deleted")).intValue() != 0) throw conflict("CONTENT_DELETED");
        long version = ((Number)release.get("source_version")).longValue() + 1;
        String snapshotId = UUID.randomUUID().toString();
        Map<String,Object> facts = new LinkedHashMap<>();
        facts.put("title", Objects.toString(drama.getTitle(), ""));
        facts.put("description", Objects.toString(drama.getDescription(), ""));
        facts.put("storyLine", Objects.toString(drama.getStoryLine(), ""));
        facts.put("cover", Objects.toString(drama.getCover(), ""));
        facts.put("actors", actors.getDramaActorsInfo(dramaId).stream().map(a -> Map.of(
            "name", Objects.toString(a.getActorName(), ""), "role", Objects.toString(a.getRoleName(), ""),
            "avatar", Objects.toString(a.getAvatar(), ""))).sorted(Comparator.comparing(this::encode)).toList());
        facts.put("tags", tags.getDramaTagsByDramaId(dramaId).stream().map(Tags::getName).sorted().toList());
        facts.put("categories", jdbc.queryForList("SELECT c.name FROM categories c JOIN drama_categories dc ON dc.category_id=c.category_id WHERE dc.drama_id=? ORDER BY c.category_id", String.class, dramaId));
        List<Episodes> sourceEpisodes = episodes.selectList(Wrappers.lambdaQuery(Episodes.class)
            .eq(Episodes::getDramaId, dramaId).orderByAsc(Episodes::getEpisodeNumber));
        if (sourceEpisodes.isEmpty()) throw conflict("EPISODES_REQUIRED");
        List<Map<String,Object>> episodeFacts = new ArrayList<>();
        for (Episodes episode : sourceEpisodes) {
            Map<String,Object> item = new LinkedHashMap<>();
            item.put("episodeId", episode.getEpisodeId().toString());
            item.put("episodeNumber", episode.getEpisodeNumber());
            item.put("description", Objects.toString(episode.getDescription(), ""));
            item.put("title", Objects.toString(episode.getTitle(), ""));
            item.put("cover", Objects.toString(episode.getCover(), ""));
            item.put("duration", episode.getDuration() != null && episode.getDuration() > 0 ? episode.getDuration() : null);
            item.put("media", asset(episode.getVideoUrl()));
            item.put("subtitle", episode.getSubtitleUrl() == null || episode.getSubtitleUrl().isBlank() ? null : asset(episode.getSubtitleUrl()));
            episodeFacts.add(item);
        }
        facts.put("episodes", episodeFacts);
        // Stable canonical serialization; media address refreshes never change registered identity.
        Map<String,Object> identities = decode(encode(facts));
        for (Map<String,Object> item : (List<Map<String,Object>>)identities.get("episodes")) {
            ((Map<String,Object>)item.get("media")).remove("url");
            if (item.get("subtitle") != null) ((Map<String,Object>)item.get("subtitle")).remove("url");
        }
        String sourceHash = hash(encode(identities));
        if (((Number)release.get("source_version")).longValue() > 0) {
            Map<String,Object> prior=jdbc.queryForMap("SELECT snapshot_id,source_hash,profile,pipeline_version FROM kcat_rag_snapshot WHERE drama_id=? AND source_version=?",dramaId,release.get("source_version"));
            if (!force && sourceHash.equals(prior.get("source_hash")) && properties.getEmbeddingProfile().equals(prior.get("profile")) && properties.getPipelineVersion().equals(prior.get("pipeline_version"))) return prior.get("snapshot_id").toString();
        }
        facts.put("snapshotId", snapshotId);
        facts.put("dramaId", dramaId.toString());
        facts.put("sourceVersion", version);
        facts.put("sourceHash", sourceHash);
        jdbc.update("INSERT INTO kcat_rag_snapshot(snapshot_id,drama_id,source_version,source_hash,snapshot_json,profile,pipeline_version) VALUES(?,?,?,?,?,?,?)",
            snapshotId, dramaId, version, sourceHash, encode(facts), properties.getEmbeddingProfile(), properties.getPipelineVersion());
        jdbc.update("UPDATE kcat_rag_release SET source_version=? WHERE drama_id=?", version, dramaId);
        // A draft edit leaves the old approved, published facts online.
        if (release.get("published_snapshot_id") == null) {
            jdbc.update("UPDATE dramas SET status=2,audit_status=0 WHERE drama_id=?", dramaId);
        }
        for (Map<String,Object> e : episodeFacts) {
            Map<?,?> media = (Map<?,?>)e.get("media");
            jdbc.update("INSERT INTO kcat_rag_media(snapshot_id,episode_id,media_identity) VALUES(?,?,?)", snapshotId, e.get("episodeId"), media.get("identity"));
        }
        Map<String,Object> variables = new LinkedHashMap<>();
        variables.put("snapshotId", snapshotId); variables.put("dramaId", dramaId.toString()); variables.put("sourceVersion", version);
        variables.put("pipelineVersion", properties.getPipelineVersion()); variables.put("embeddingProfile", properties.getEmbeddingProfile());
        variables.put("dramaName", drama.getTitle()); variables.put("description", Objects.toString(drama.getDescription(), ""));
        enqueue(snapshotId, "START", variables);
        return snapshotId;
    }

    private Map<String,Object> asset(String url) {
        if (url == null || url.isBlank()) throw conflict("MEDIA_REQUIRED");
        URI uri = URI.create(url);
        if (!Set.of("http", "https").contains(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null)
            throw conflict("MEDIA_URL_REJECTED");
        String address = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
        List<Map<String,Object>> found = jdbc.queryForList("SELECT identity,sha256 FROM kcat_rag_asset WHERE url_hash=?", hash(address));
        if (found.isEmpty()) throw conflict("MEDIA_IDENTITY_REQUIRED_UPLOAD_OR_REGISTER_CHECKSUM");
        Map<String,Object> asset = new LinkedHashMap<>(found.getFirst());
        asset.put("url", url);
        return asset;
    }

    public void registerAsset(String url, String identity, String sha256, long size) {
        if (!enabled()) return;
        URI uri = URI.create(url);
        String address = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
        jdbc.update("INSERT INTO kcat_rag_asset(url_hash,identity,sha256,size_bytes) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE identity=VALUES(identity),sha256=VALUES(sha256),size_bytes=VALUES(size_bytes)",
            hash(address), identity, sha256, size);
    }

    public void enqueue(String snapshotId, String type, Map<String,Object> body) {
        jdbc.update("INSERT IGNORE INTO kcat_rag_outbox(event_id,event_type,snapshot_id,payload_json) VALUES(?,?,?,?)",
            UUID.randomUUID().toString(), type, snapshotId, encode(body));
    }

    public Map<String,Object> snapshot(String id) {
        Map<String,Object> row = current(id, false);
        if (!"APPROVED".equals(row.get("audit_state"))) throw conflict("SNAPSHOT_NOT_APPROVED");
        return decode((String)row.get("snapshot_json"));
    }

    public Map<String,Object> current(String snapshotId, boolean lock) {
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT s.*,r.source_version AS latest_version,r.deleted FROM kcat_rag_snapshot s JOIN kcat_rag_release r ON r.drama_id=s.drama_id WHERE s.snapshot_id=?" + (lock ? " FOR UPDATE" : ""), snapshotId);
        if (rows.isEmpty()) throw conflict("SNAPSHOT_UNKNOWN");
        Map<String,Object> row = rows.getFirst();
        if (((Number)row.get("deleted")).intValue() != 0 || !row.get("source_version").equals(row.get("latest_version")))
            throw conflict("SUPERSEDED");
        return row;
    }

    @Transactional
    public void decision(String snapshotId, boolean approved, String reason, String userName) {
        Map<String,Object> row = current(snapshotId, true);
        String state = approved ? "APPROVED" : "REJECTED";
        if (!"PENDING".equals(row.get("audit_state")) && !state.equals(row.get("audit_state"))) throw conflict("DECISION_CONFLICT");
        jdbc.update("UPDATE kcat_rag_snapshot SET audit_state=? WHERE snapshot_id=?", state, snapshotId);
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("processId", row.get("process_id")); body.put("snapshotId", snapshotId); body.put("approve", approved);
        body.put("auditReason", Objects.toString(reason, "")); body.put("userName", userName);
        enqueue(snapshotId, "DECISION", body);
    }

    @Transactional
    public Map<String,Object> indexResult(String buildId, Map<String,Object> request) {
        String id = request.get("snapshotId").toString();
        Map<String,Object> row = current(id, true);
        if (!"APPROVED".equals(row.get("audit_state")) || !"READY".equals(request.get("state")) ||
            !row.get("drama_id").toString().equals(request.get("dramaId")) ||
            ((Number)row.get("source_version")).longValue() != ((Number)request.get("sourceVersion")).longValue() ||
            !row.get("profile").equals(request.get("embeddingProfile")) || !row.get("pipeline_version").equals(request.get("pipelineVersion")))
            throw conflict("INDEX_AUTHORIZATION_CONFLICT");
        if (row.get("build_id") != null && !row.get("build_id").equals(buildId)) throw conflict("BUILD_CONFLICT_REBUILD_REQUIRES_NEW_VERSION");
        if (row.get("build_id") == null) {
            List<?> reported = (List<?>)request.get("episodeIds");
            List<Map<String,Object>> facts = (List<Map<String,Object>>)decode((String)row.get("snapshot_json")).get("episodes");
            if (reported == null || !new HashSet<>(reported).equals(new HashSet<>(facts.stream().map(e -> e.get("episodeId")).toList())) ||
                ((Number)request.getOrDefault("documentCount", 0)).intValue() < facts.size()) throw conflict("INCOMPLETE_INDEX_MANIFEST");
        }
        jdbc.update("UPDATE kcat_rag_snapshot SET index_state='READY',build_id=? WHERE snapshot_id=?", buildId, id);
        return Map.of("state", "READY");
    }

    @Transactional
    public Map<String,Object> finish(String id, boolean approved) {
        Map<String,Object> row = current(id, true);
        if (!approved) {
            if (!"REJECTED".equals(row.get("audit_state"))) throw conflict("REJECTION_NOT_RECORDED");
            jdbc.update("UPDATE dramas SET audit_status=2,status=0 WHERE drama_id=? AND NOT EXISTS (SELECT 1 FROM kcat_rag_release WHERE drama_id=? AND published_snapshot_id IS NOT NULL)", row.get("drama_id"), row.get("drama_id"));
            return Map.of("state", "REJECTED");
        }
        if (!"APPROVED".equals(row.get("audit_state")) || !"READY".equals(row.get("media_state")) || !"READY".equals(row.get("index_state")))
            throw conflict("PUBLICATION_NOT_READY");
        jdbc.update("UPDATE kcat_rag_release SET published_source_version=?,published_snapshot_id=?,published_build_id=?,embedding_profile=? WHERE drama_id=? AND source_version=? AND deleted=0",
            row.get("source_version"), id, row.get("build_id"), row.get("profile"), row.get("drama_id"), row.get("source_version"));
        jdbc.update("UPDATE dramas d JOIN kcat_rag_release r ON r.drama_id=d.drama_id SET d.audit_status=1,d.status=IF(r.desired_on_shelf=1,1,0) WHERE d.drama_id=?", row.get("drama_id"));
        return Map.of("state", "PUBLISHED", "sourceVersion", row.get("source_version"));
    }

    public Map<String,Object> published(Long dramaId) {
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT s.snapshot_json,r.published_source_version,r.published_build_id,r.embedding_profile FROM kcat_rag_release r JOIN kcat_rag_snapshot s ON s.snapshot_id=r.published_snapshot_id JOIN dramas d ON d.drama_id=r.drama_id WHERE r.drama_id=? AND r.deleted=0 AND r.desired_on_shelf=1 AND d.status=1 AND d.audit_status=1 AND s.audit_state='APPROVED' AND s.media_state='READY' AND s.index_state='READY'", dramaId);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.GONE, "CONTENT_NOT_PUBLISHED");
        Map<String,Object> row = rows.getFirst();
        Map<String,Object> facts = decode((String)row.get("snapshot_json"));
        facts.put("buildId", row.get("published_build_id")); facts.put("embeddingProfile", row.get("embedding_profile"));
        return facts;
    }

    public Map<String,Object> publishedBuilds(String profile, Long after) {
        if (!profile.matches("p_[a-f0-9]{20}") || after < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"INVALID_PROFILE_CURSOR");
        if (jdbc.queryForObject("SELECT COUNT(*) FROM kcat_rag_release r JOIN dramas d ON d.drama_id=r.drama_id WHERE r.deleted=0 AND r.desired_on_shelf=1 AND d.status=1 AND d.audit_status=1 AND r.published_build_id IS NOT NULL AND r.embedding_profile<>?",Long.class,profile)>0)
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"MULTIPLE_PROFILES_REQUIRE_SEPARATE_QUERY_SERVICES");
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT r.drama_id,r.published_build_id AS buildId FROM kcat_rag_release r JOIN dramas d ON d.drama_id=r.drama_id JOIN kcat_rag_snapshot s ON s.snapshot_id=r.published_snapshot_id WHERE r.embedding_profile=? AND r.drama_id>? AND r.deleted=0 AND r.desired_on_shelf=1 AND d.status=1 AND d.audit_status=1 AND s.audit_state='APPROVED' AND s.media_state='READY' AND s.index_state='READY' ORDER BY r.drama_id LIMIT 500",profile,after);
        return Map.of("items",rows,"next",rows.size()==500 ? rows.getLast().get("drama_id").toString() : "");
    }

    public Map<String,Object> buildStatus(String buildId) {
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT s.source_version,r.source_version AS latest,r.published_build_id,r.deleted FROM kcat_rag_snapshot s JOIN kcat_rag_release r ON r.drama_id=s.drama_id WHERE s.build_id=?",buildId);
        if (rows.isEmpty()) return Map.of("retirable",false,"state","UNREGISTERED");
        Map<String,Object> row=rows.getFirst();
        boolean published=buildId.equals(row.get("published_build_id"));
        boolean current=row.get("source_version").equals(row.get("latest"));
        return Map.of("retirable",!published && !current,"published",published,"currentDraft",current,"deleted",row.get("deleted"));
    }

    public boolean managed(Long id) {
        return enabled() && jdbc.queryForObject("SELECT COUNT(*) FROM kcat_rag_release WHERE drama_id=? AND deleted=0",Long.class,id)>0;
    }

    public Map<String,Object> draft(Long dramaId) {
        List<Map<String,Object>> rows=jdbc.queryForList("SELECT s.snapshot_id AS snapshotId,s.process_id AS processId,s.source_version AS sourceVersion,s.audit_state AS auditState,s.media_state AS mediaState,s.index_state AS indexState,s.snapshot_json FROM kcat_rag_snapshot s JOIN kcat_rag_release r ON r.drama_id=s.drama_id AND r.source_version=s.source_version WHERE r.drama_id=? AND r.deleted=0",dramaId);
        if (rows.isEmpty()) throw conflict("NO_MANAGED_DRAFT");
        Map<String,Object> row=new LinkedHashMap<>(rows.getFirst());row.put("facts",decode(row.remove("snapshot_json").toString()));return row;
    }

    public List<Map<String,Object>> validate(List<Map<String,Object>> requests) {
        if (requests.size() > 240) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TOO_MANY_CANDIDATES");
        List<Map<String,Object>> result = new ArrayList<>();
        for (Map<String,Object> c : requests) {
            Map<String,Object> item = new LinkedHashMap<>(c);
            item.put("valid", false);
            try {
                Map<String,Object> p = published(Long.valueOf(c.get("dramaId").toString()));
                List<Map<String,Object>> eps = (List<Map<String,Object>>)p.get("episodes");
                boolean episodeValid = c.get("episodeId") == null || eps.stream().anyMatch(e -> e.get("episodeId").equals(c.get("episodeId")));
                if (p.get("sourceVersion").toString().equals(c.get("sourceVersion").toString()) && p.get("buildId").equals(c.get("buildId")) && p.get("embeddingProfile").equals(c.get("embeddingProfile")) && episodeValid) {
                    item.put("valid", true); item.put("title", p.get("title")); item.put("cover", p.get("cover")); item.put("actors", p.get("actors"));
                }
            } catch (ResponseStatusException ignored) { }
            result.add(item);
        }
        return result;
    }

    @Transactional
    public void shelfIntent(Long id, Long status) {
        if (!enabled() || status == null) return;
        jdbc.update("UPDATE kcat_rag_release SET desired_on_shelf=? WHERE drama_id=?", status == 1 ? 1 : 0, id);
    }

    public void deleted(Collection<Long> ids) {
        if (enabled()) for (Long id : ids) jdbc.update("UPDATE kcat_rag_release SET deleted=1,desired_on_shelf=0 WHERE drama_id=?", id);
    }

    @Transactional
    public void refreshIfManaged(Long dramaId) {
        if (!enabled()) return;
        if (jdbc.queryForObject("SELECT COUNT(*) FROM kcat_rag_release WHERE drama_id=? AND deleted=0",Long.class,dramaId)>0) capture(dramaId);
    }
}

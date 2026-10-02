package com.lfy.kcat.content.controller;

import com.lfy.kcat.content.biz.RagMediaService;
import com.lfy.kcat.content.biz.RagPublishedViews;
import com.lfy.kcat.content.biz.RagReleaseService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author liaochengjie
 */
@RestController
@RequestMapping("/internal/rag")
@RequiredArgsConstructor
public class RagInternalController {
    private final RagReleaseService release;
    private final RagMediaService media;
    private final RagPublishedViews views;

    @GetMapping("/published-builds")
    public Map<String,Object> builds(@RequestParam String profile,@RequestParam(defaultValue="0") Long after) { return release.publishedBuilds(profile,after); }

    @GetMapping("/builds/{buildId}")
    public Map<String,Object> build(@PathVariable String buildId) { return release.buildStatus(buildId); }

    @PostMapping("/dramas/{dramaId}/rebuild")
    public Map<String,Object> rebuild(@PathVariable Long dramaId) { return Map.of("snapshotId",release.capture(dramaId,true),"state","PENDING_REAPPROVAL"); }

    @PostMapping("/snapshots/{snapshotId}/media/retry")
    public Map<String,Object> retryMedia(@PathVariable String snapshotId) { return media.retry(snapshotId); }

    @GetMapping("/published/{dramaId}/episodes")
    public org.dromara.common.core.dto.home.HomeDramaEpisodesDTO published(@PathVariable Long dramaId) {
        return views.episodes(dramaId);
    }

    @GetMapping("/health")
    public Map<String,Object> health() {
        return release.health();
    }

    @GetMapping("/snapshots/{snapshotId}")
    public Map<String,Object> snapshot(@PathVariable String snapshotId) { return release.snapshot(snapshotId); }

    @PostMapping("/content/validate")
    public Map<String,Object> validate(@RequestBody Map<String,List<Map<String,Object>>> body) {
        return Map.of("items", release.validate(body.getOrDefault("candidates", List.of())));
    }

    @PutMapping("/index-results/{buildId}")
    public Map<String,Object> result(@PathVariable String buildId, @RequestBody Map<String,Object> body) {
        return release.indexResult(buildId, body);
    }

    @PostMapping("/snapshots/{snapshotId}/finish")
    public Map<String,Object> finish(@PathVariable String snapshotId, @RequestBody Map<String,Object> body) {
        return release.finish(snapshotId, Boolean.TRUE.equals(body.get("approve")));
    }

    @PostMapping("/snapshots/{snapshotId}/media")
    public Map<String,Object> startMedia(@PathVariable String snapshotId) { return media.start(snapshotId); }

    @GetMapping("/snapshots/{snapshotId}/media")
    public Map<String,Object> media(@PathVariable String snapshotId) { return media.status(snapshotId); }

    @PostMapping("/assets")
    public Map<String,Object> asset(@RequestBody Map<String,Object> body) {
        String sha = body.get("sha256").toString();
        if (!sha.matches("[a-f0-9]{64}")) throw new IllegalArgumentException("SHA256_REQUIRED");
        release.registerAsset(body.get("url").toString(), body.get("identity").toString(), sha, ((Number)body.get("sizeBytes")).longValue());
        return Map.of("state", "REGISTERED");
    }

    @PostMapping("/outbox/{eventId}/retry")
    public Map<String,Object> retry(@PathVariable String eventId) {
        return release.retryOutbox(eventId);
    }
}

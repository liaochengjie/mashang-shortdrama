package com.lfy.kcat.content.biz;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 管理固定内容快照、审核及上架状态。
 *
 * @author liaochengjie
 */
public interface RagReleaseService {
    boolean enabled();
    Map<String,Object> health();
    Map<String,Object> retryOutbox(String eventId);
    String encode(Object value);
    Map<String,Object> decode(String value);
    String capture(Long dramaId);
    String capture(Long dramaId, boolean force);
    void registerAsset(String url, String identity, String sha256, long size);
    void enqueue(String snapshotId, String type, Map<String,Object> body);
    Map<String,Object> snapshot(String id);
    Map<String,Object> current(String snapshotId, boolean lock);
    void decision(String snapshotId, boolean approved, String reason, String userName);
    Map<String,Object> indexResult(String buildId, Map<String,Object> request);
    Map<String,Object> finish(String id, boolean approved);
    Map<String,Object> published(Long dramaId);
    Map<String,Object> publishedBuilds(String profile, Long after);
    Map<String,Object> buildStatus(String buildId);
    boolean managed(Long id);
    Map<String,Object> draft(Long dramaId);
    List<Map<String,Object>> validate(List<Map<String,Object>> requests);
    void shelfIntent(Long id, Long status);
    void deleted(Collection<Long> ids);
    void refreshIfManaged(Long dramaId);
}

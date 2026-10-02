package com.lfy.kcat.user.business;

import java.util.Map;

/**
 * 情节搜索及发布版本播放校验。
 *
 * @author liaochengjie
 */
public interface RagAppService {
    Map search(String keyword, int page, int pageSize, String sessionId);

    Map playback(String dramaId, Long sourceVersion, String buildId, String embeddingProfile, String episodeId);
}

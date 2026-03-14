package org.dromara.common.core.dto.evert;

import lombok.Data;

@Data
public class LikeEvent extends BaseEvent {
    // 剧集ID
    private Long episodeId;
    // 操作类型（like/unlike）
    private String action;
}

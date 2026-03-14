package org.dromara.common.core.dto.evert;

import lombok.Data;
@Data
public class BaseEvent {
    //雪花算法
    private Long eventId;

    private Long userId;

    private Long currentTimeMillis;

    private String eventType;
}

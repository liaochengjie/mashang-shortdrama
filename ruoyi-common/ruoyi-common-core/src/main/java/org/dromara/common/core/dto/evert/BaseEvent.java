package org.dromara.common.core.dto.evert;

import lombok.Data;
@Data
public class BaseEvent {
    //雪花算法
    private Long eventId;

    private Long userId;
    //当前时间戳
    private Long currentTimeMillis;
    //事件类型
    private String eventType;
}

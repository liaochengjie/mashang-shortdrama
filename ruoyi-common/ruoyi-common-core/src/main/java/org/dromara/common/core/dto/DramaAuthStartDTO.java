package org.dromara.common.core.dto;

import lombok.Data;

/**
 * 这个是短剧自动审核的DTO
 */
@Data
public class DramaAuthStartDTO {
    private Long dramaId;
    private String dramaName;
    private String description;
}

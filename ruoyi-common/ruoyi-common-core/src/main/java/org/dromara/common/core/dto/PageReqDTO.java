package org.dromara.common.core.dto;

import lombok.Data;

@Data
public class PageReqDTO {
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String timeRange;
}

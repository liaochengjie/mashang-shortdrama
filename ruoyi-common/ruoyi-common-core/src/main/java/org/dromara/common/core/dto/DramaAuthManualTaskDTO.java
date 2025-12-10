package org.dromara.common.core.dto;

import lombok.Data;

@Data
public class DramaAuthManualTaskDTO {
    private Long dramaId;
    private String userName;
    private String processId;

    //审核不通过的原因
    private String auditReason;
    //人工审核状态
    private String auditStatus;
}

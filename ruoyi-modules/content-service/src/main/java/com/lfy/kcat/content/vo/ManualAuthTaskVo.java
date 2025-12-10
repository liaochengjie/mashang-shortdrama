package com.lfy.kcat.content.vo;

import lombok.Data;

@Data
public class ManualAuthTaskVo {

    private Long DramaId;
    private String auditReason;
    private String auditStatus;//人工审核
    private String processId;
}

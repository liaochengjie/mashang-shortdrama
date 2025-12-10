package org.dromara.common.core.dto;

import lombok.Data;

@Data
public class DramaAuthCompleteDTO {
    private Long dramaId;
    //人工审核不通过的原因
    private String auditReason;
    //人工审核状态
    private String auditStatus;

    private Boolean approve;

    //AI审核剧名状态 0负面 1正面
    private  String authName;
    //AI审核短剧描述状态 0负面 1正面
    private String authStatus;
}

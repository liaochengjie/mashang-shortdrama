package com.lfy.kcat.content.domain.vo;

import lombok.Data;

/**
 * @author 廖成杰
 * @date 2025/11/4
 */
@Data
public class PublishActorsVo {
    private Long actorId;
    private String actorName;
    private String roleName;
    private Integer roleId;
    private Boolean isNewActors;
    private Integer sortOrder;
}

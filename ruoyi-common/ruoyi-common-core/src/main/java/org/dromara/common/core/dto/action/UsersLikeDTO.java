package org.dromara.common.core.dto.action;

import lombok.Data;

@Data
public class UsersLikeDTO {
    private Long userId;
    private Long episodeId;
     private String like;
}

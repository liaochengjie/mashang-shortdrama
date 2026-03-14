package org.dromara.common.core.dto.action;

import lombok.Data;

@Data
public class CommentsPageDTO {
    //当前页
    private Integer page;
    //每页数量
    private Integer pageSize;
    //排序字段
    private String sort;
    //是否显示剧透
    private Boolean showSpoilers;

    private String episodesId;
}

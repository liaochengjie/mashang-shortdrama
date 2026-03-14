package org.dromara.common.core.dto.action;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class CommentDTO {
    private List<CommentsDTO> comments;

    @NoArgsConstructor
    @Data
    public static class CommentsDTO {
        //使用字符型id，避免前端js精度问题
        private String id;
        private String user;
        private String timestamp;
        private String content;
        private Boolean isLiked;
        private Integer likeCount;

    }
}

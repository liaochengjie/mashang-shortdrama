package com.lfy.kcat.user.business;

import org.dromara.common.core.dto.action.CommentDTO;
import org.dromara.common.core.dto.action.CommentsPageDTO;

public interface UsersCommentService {
    CommentDTO getComments(CommentsPageDTO commentsPageDTO);
}

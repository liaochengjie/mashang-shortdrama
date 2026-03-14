package com.lfy.kcat.interaction.business;

import org.dromara.common.core.dto.action.CommentsPageDTO;
import org.dromara.common.core.dto.action.CommentDTO;

public interface UsersCommentSerivce {

    CommentDTO getComments(CommentsPageDTO commentsPageDTO);
}

package com.lfy.kcat.user.business.impl;

import com.lfy.kcat.user.business.UsersCommentService;
import com.lfy.kcat.user.feign.InteractionServiceFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.dto.action.CommentDTO;
import org.dromara.common.core.dto.action.CommentsPageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsersCommentServiceImpl implements UsersCommentService {
    @Autowired
    private InteractionServiceFeignClient interactionServiceFeignClient;
    @Override
    public CommentDTO getComments(CommentsPageDTO commentsPageDTO) {
        Integer page = commentsPageDTO.getPage();
        Integer pageSize = commentsPageDTO.getPageSize();
        String episodesId = commentsPageDTO.getEpisodesId();
        log.info("准备远程调用获取评论列表，参数：page={}, pageSize={}, episodesId={}", page, pageSize, episodesId);
        CommentDTO data = interactionServiceFeignClient.getComments(page, pageSize, episodesId).getData();
        return data;
    }
}

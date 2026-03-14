package com.lfy.kcat.user.controller;

import com.lfy.kcat.user.business.UsersCommentService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.action.CommentDTO;
import org.dromara.common.core.dto.action.CommentsPageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class UsersCommentController {
    @Autowired
    UsersCommentService usersCommentService;
    @GetMapping("/episodes/{episodesId}/comments")
    public R<CommentDTO> getComments(@RequestParam("page") Integer page,
                         @RequestParam("pageSize") Integer pageSize,
                         @PathVariable("episodesId") String episodesId){
        CommentsPageDTO commentsPageDTO = new CommentsPageDTO();
        commentsPageDTO.setPage(page);
        commentsPageDTO.setPageSize(pageSize);
        commentsPageDTO.setEpisodesId(episodesId);
        CommentDTO commentDTO = usersCommentService.getComments(commentsPageDTO);
        log.info("成功获取评论信息，评论列表：{}", commentDTO);
        return R.ok(commentDTO);
    }
}

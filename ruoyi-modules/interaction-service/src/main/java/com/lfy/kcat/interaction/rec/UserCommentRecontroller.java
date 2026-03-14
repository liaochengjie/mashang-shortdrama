package com.lfy.kcat.interaction.rec;

import com.lfy.kcat.interaction.business.UsersCommentSerivce;
import org.dromara.common.core.dto.action.CommentsPageDTO;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.action.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserCommentRecontroller {

    @Autowired
    private UsersCommentSerivce usersCommentSerivce;
    @GetMapping("/episodes/comments")
    public R getComments(@RequestParam("page") Integer page,
                         @RequestParam("pageSize") Integer pageSize,
                         @RequestParam("episodesId") String episodesId){
        CommentsPageDTO commentsPageDTO = new CommentsPageDTO();
        commentsPageDTO.setPage(page);
        commentsPageDTO.setPageSize(pageSize);
        commentsPageDTO.setEpisodesId(episodesId);
        CommentDTO comments = usersCommentSerivce.getComments(commentsPageDTO);
        return R.ok(comments);
    }

}

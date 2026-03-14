package com.lfy.kcat.interaction.business.Impl;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfy.kcat.interaction.business.UsersCommentSerivce;
import com.lfy.kcat.interaction.domain.Comments;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.dto.action.CommentsPageDTO;
import com.lfy.kcat.interaction.service.CommentsService;
import org.dromara.common.core.dto.action.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UsersCommentImpl implements UsersCommentSerivce {
    @Autowired
    CommentsService commentsService;

    @Override
    public CommentDTO getComments(CommentsPageDTO commentsPageDTO) {
        Integer page = commentsPageDTO.getPage();
        Integer pageSize = commentsPageDTO.getPageSize();
        Page<Comments> commentsPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Comments> eq = Wrappers.lambdaQuery(Comments.class)
            .eq(Comments::getAuditStatus, 1)
            .eq(Comments::getAuditStatus, 1)
            .eq(Comments::getParentId, 0L)
            .eq(Comments::getEpisodeId, commentsPageDTO.getEpisodesId());
        List<Comments> list = commentsService.list(commentsPage, eq);
        //如果评论消息不为空那么就转换为评论DTO
        if(list!=null&&list.size()>0) {
            log.info("评论消息不为空，正在转换为评论DTO");
            List<CommentDTO.CommentsDTO> list1 = list.stream().map(comments -> {
                CommentDTO.CommentsDTO commentsDTO = new CommentDTO.CommentsDTO();
                commentsDTO.setId(comments.getCommentId().toString());
                commentsDTO.setUser(comments.getUserId().toString());
                Date createTime = comments.getCreateTime();
                String format = DateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss");
                commentsDTO.setTimestamp(format);
                commentsDTO.setContent(comments.getContent());
                commentsDTO.setIsLiked(false);
                commentsDTO.setLikeCount(comments.getLikeCount());
                return commentsDTO;
            }).toList();
            //将评论DTO列表设置到评论分页DTO中
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setComments(list1);
            return commentDTO;
        }
        //如果消息为空那么就返回空的评论分页DTO
        return new CommentDTO();
    }
}

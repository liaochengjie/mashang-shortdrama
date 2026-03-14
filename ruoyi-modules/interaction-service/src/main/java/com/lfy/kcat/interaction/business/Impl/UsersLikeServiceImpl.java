package com.lfy.kcat.interaction.business.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.lfy.kcat.interaction.business.UsersLikeService;
import com.lfy.kcat.interaction.constant.LikeEpisode;
import com.lfy.kcat.interaction.domain.UserLikes;
import com.lfy.kcat.interaction.service.UserLikesService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.dto.action.UsersLikeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UsersLikeServiceImpl implements UsersLikeService {
    @Autowired
    UserLikesService userLikesService;
    @Override
    public void likeEpisode(UsersLikeDTO usersLikeDTO) {
        Long userId = usersLikeDTO.getUserId();
        Long episodeId = usersLikeDTO.getEpisodeId();
        String like = usersLikeDTO.getLike();
        LambdaQueryWrapper<UserLikes> eq = Wrappers.lambdaQuery(UserLikes.class)
            .eq(UserLikes::getUserId, userId)
            .eq(UserLikes::getEpisodeId, episodeId)
            .eq(UserLikes::getTargetType, LikeEpisode.LIKE_EPISODE);
        long count = userLikesService.count(eq);
        boolean equals = "like".equals(like);

        if(count>0){
            log.info("存在数据库点赞记录，现在进行更新");
            log.info("用户{}对剧集{}的点赞状态为{}",userId,episodeId,equals);
            //说明数据库已经存在点赞记录了，需要根据like判断是否需要取消点赞
            LambdaUpdateWrapper<UserLikes> eq1 = Wrappers.lambdaUpdate(UserLikes.class)
                .set(UserLikes::getStatus, equals?1:0)
                .eq(UserLikes::getUserId, userId)
                .eq(UserLikes::getEpisodeId, episodeId)
                .eq(UserLikes::getTargetType, LikeEpisode.LIKE_EPISODE);
            userLikesService.update(eq1);

        }else {
            //说明数据库不存在点赞记录，需要新增
            log.info("未存在数据库点赞记录，现在进行新增");
            log.info("用户{}对剧集{}的点赞状态为{}",userId,episodeId,equals);
            UserLikes userLikes = new UserLikes();
            userLikes.setUserId(userId);
            userLikes.setEpisodeId(episodeId);
            userLikes.setStatus(equals?1:0);
            userLikes.setTargetType(LikeEpisode.LIKE_EPISODE);
            userLikes.setTargetId(episodeId);
            userLikesService.save(userLikes);
        }

    }
}

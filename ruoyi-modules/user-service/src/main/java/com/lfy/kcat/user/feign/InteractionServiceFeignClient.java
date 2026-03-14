package com.lfy.kcat.user.feign;

import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.action.CommentDTO;
import org.dromara.common.core.dto.action.UsersLikeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "interaction-service")
public interface InteractionServiceFeignClient {

    /**
     * 获取剧集评论
     * @param
     * @return
     */
    @GetMapping("/episodes/comments")
    R<CommentDTO> getComments(@RequestParam("page") Integer page,
                         @RequestParam("pageSize") Integer pageSize,
                         @RequestParam("episodesId") String episodesId);

    /**
     * 点赞剧集
     * @param usersLikeDTO
     * @return
     */
    @PostMapping("/action/episodes/like")
    R likeEpisode(@RequestBody UsersLikeDTO usersLikeDTO);


}

package com.lfy.kcat.user.controller;

import com.lfy.kcat.user.business.UsersLikeService;
import lombok.Data;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.action.UsersLikeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UsersLikeController {
    @Data
    public static class LikesStatus{
        String action;
    }


    @Autowired
    private UsersLikeService usersLikeService;
    @PostMapping("/episodes/{episodesId}/like")
    public R likeEpisode(@RequestBody LikesStatus likesStatus,
                         @PathVariable("episodesId") Long episodesId){
        UsersLikeDTO usersLikeDTO = new UsersLikeDTO();
        usersLikeDTO.setEpisodeId(episodesId);
        usersLikeDTO.setLike(likesStatus.getAction());
        usersLikeService.likeEpisode(usersLikeDTO);
        return R.ok();
    }

}

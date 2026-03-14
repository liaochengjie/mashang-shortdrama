package com.lfy.kcat.interaction.rec;


import com.lfy.kcat.interaction.business.UsersLikeService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.action.UsersLikeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersLikeRecontroller {

    @Autowired
    UsersLikeService usersLikeService;
    @PostMapping("/action/episodes/like")
    public R likeEpisode(@RequestBody UsersLikeDTO usersLikeDTO){
        usersLikeService.likeEpisode(usersLikeDTO);
        return R.ok();
    }

}

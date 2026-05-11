package com.lfy.kcat.user.controller;

import com.lfy.kcat.user.business.DramaBizService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class AppHomeController {

    @Autowired
    DramaBizService dramaBizService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取首页精选数据
     * @param pageReqDTO
     * @return
     */
    @GetMapping("/episodes/featured")
    public R featured(PageReqDTO pageReqDTO){

        HomeFeaturedDTO homeFeaturedDTO=dramaBizService.getHomeFeature(pageReqDTO);
        homeFeaturedDTO.getEpisodes().stream().forEach(episodesDTO -> {
            //通过dramaBizService来查询是否点赞过
            Boolean isLiked = dramaBizService.getUserIsLike(episodesDTO.getEpisode());
            episodesDTO.setIsLiked(isLiked);
        });

        //模拟数据
//        HomeFeaturedDTO mock = JMockData.mock(HomeFeaturedDTO.class);
//        mock.getEpisodes().forEach(episode->{
//            episode.setVideoUrl("https://1500043826.vod-qcloud.com/b2165004vodtransgzp1500043826/a5a0e79d5145403710509137980/v.f101301.mp4");
//        });
        return R.ok(homeFeaturedDTO);
    }

    /**
     * 获取剧集详情数据
     * @param dramaId
     * @return
     */
    @GetMapping("/dramas/{dramaId}/episodes/all")
    public R dramaEpisodes(@PathVariable("dramaId") Long dramaId){
        HomeDramaEpisodesDTO homeDramaEpisodesDTO=dramaBizService.getDramaEpisodes(dramaId);
        return R.ok(homeDramaEpisodesDTO);
    }

    /**
     * 获取剧集详情数据
     * @param dramaId
     * @return
     */
    @GetMapping("/dramas/{dramaId}")
    public R dramaInfo(@PathVariable("dramaId") Long dramaId){
        HomeDramaInfoDTO homeDramaInfoDTO = dramaBizService.getDramaInfo(dramaId);
        return R.ok(homeDramaInfoDTO);
    }
}

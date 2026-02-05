package com.lfy.kcat.user.controller;

import com.github.jsonzou.jmockdata.JMockData;
import com.lfy.kcat.user.business.DramaBizService;
import com.lfy.kcat.user.vo.PageReqVo;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.HomeDramaInfoDTO;
import org.dromara.common.core.dto.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppHomeController {

    @Autowired
    DramaBizService dramaBizService;
    @GetMapping("/episodes/featured")
    public R featured(PageReqDTO pageReqDTO){

        HomeFeaturedDTO homeFeaturedDTO=dramaBizService.getHomeFeature(pageReqDTO);
        //模拟数据
//        HomeFeaturedDTO mock = JMockData.mock(HomeFeaturedDTO.class);
//        mock.getEpisodes().forEach(episode->{
//            episode.setVideoUrl("https://1500043826.vod-qcloud.com/b2165004vodtransgzp1500043826/a5a0e79d5145403710509137980/v.f101301.mp4");
//        });
        return R.ok(homeFeaturedDTO);
    }

    @GetMapping("/dramas/{dramaId}/episodes/all")
    public R dramaEpisodes(@PathVariable("dramaId") Long dramaId){
        HomeDramaEpisodesDTO homeDramaEpisodesDTO=dramaBizService.getDramaEpisodes(dramaId);
        return R.ok(homeDramaEpisodesDTO);
    }

    @GetMapping("/dramas/{dramaId}")
    public R dramaInfo(@PathVariable("dramaId") Long dramaId){
        HomeDramaInfoDTO homeDramaInfoDTO = dramaBizService.getDramaInfo(dramaId);
        return R.ok(homeDramaInfoDTO);
    }
}

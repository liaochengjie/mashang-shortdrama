package com.lfy.kcat.content.rec;

import com.lfy.kcat.content.biz.DramaHomeService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class AppHomeFeatured {

    @Autowired
    DramaHomeService dramaHomeService;

    @PostMapping("/home/featured/infoflow")
    public R<HomeFeaturedDTO> featured(@RequestBody PageReqDTO pageReqDTO) {
        //获取首页精选数据
        HomeFeaturedDTO dto = dramaHomeService.getHomeFeaturedDrama(pageReqDTO);
        log.info("正在获取首页精选视频数据:{}", pageReqDTO);
        return R.ok(dto);
    }

    @GetMapping("/episodes/{dramaId}/byDramaId/all")
    public R<HomeDramaEpisodesDTO> dramaEpisodes(@PathVariable("dramaId") Long dramaId) {
        log.info("准备获取短剧所有数据,dramaId:{}", dramaId);
        HomeDramaEpisodesDTO homeDramaEpisodesDTO = dramaHomeService.getDramaEpisodes(dramaId);

        return R.ok(homeDramaEpisodesDTO);
    }

    @GetMapping("/drama/{dramaId}/info")
    public R<HomeDramaInfoDTO> dramaInfo(@PathVariable("dramaId") Long dramaId){
        HomeDramaInfoDTO dramaInfoDTO=dramaHomeService.getDramaInfo(dramaId);
        return R.ok(dramaInfoDTO);
    }
}

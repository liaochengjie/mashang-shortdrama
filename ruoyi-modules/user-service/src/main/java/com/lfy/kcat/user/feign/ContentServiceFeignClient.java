package com.lfy.kcat.user.feign;

import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "content-service")
public interface ContentServiceFeignClient {


    /**
     * 获取首页推荐短剧
     * @param pageReqDTO
     * @return
     */
    @PostMapping("/home/featured/infoflow")
    R<HomeFeaturedDTO> featured(@RequestBody PageReqDTO pageReqDTO);

    /**
     * 获取短剧所有剧集
     * @param dramaId
     * @return
     */
    @GetMapping("/episodes/{dramaId}/byDramaId/all")
    R<HomeDramaEpisodesDTO> dramaEpisodes(@PathVariable("dramaId") Long dramaId);

    /**
     * 获取短剧详情
     * @param dramaId
     * @return
     */
    @GetMapping("/drama/{dramaId}/info")
    R<HomeDramaInfoDTO> dramaInfo(@PathVariable("dramaId") Long dramaId);
}

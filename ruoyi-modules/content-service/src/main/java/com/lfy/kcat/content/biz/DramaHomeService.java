package com.lfy.kcat.content.biz;

import org.dromara.common.core.dto.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.HomeDramaInfoDTO;
import org.dromara.common.core.dto.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;
import org.springframework.stereotype.Service;

@Service
public interface DramaHomeService {
    /**
     * 获取首页精选视频数据
     * @param pageReqDTO
     * @return
     */
    HomeFeaturedDTO getHomeFeaturedDrama(PageReqDTO pageReqDTO);

    HomeDramaEpisodesDTO getDramaEpisodes(Long dramaId);

    HomeDramaInfoDTO getDramaInfo(Long dramaId);
}

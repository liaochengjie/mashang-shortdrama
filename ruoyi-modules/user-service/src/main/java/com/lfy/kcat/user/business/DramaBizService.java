package com.lfy.kcat.user.business;

import com.lfy.kcat.user.vo.PageReqVo;
import org.dromara.common.core.dto.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.HomeDramaInfoDTO;
import org.dromara.common.core.dto.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;

public interface DramaBizService {
    HomeFeaturedDTO getHomeFeature(PageReqDTO pageReqDTO);

    HomeDramaEpisodesDTO getDramaEpisodes(Long dramaId);

    HomeDramaInfoDTO getDramaInfo(Long dramaId);
}

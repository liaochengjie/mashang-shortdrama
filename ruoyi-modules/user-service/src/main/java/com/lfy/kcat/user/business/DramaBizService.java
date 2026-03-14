package com.lfy.kcat.user.business;

import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.dromara.common.core.dto.PageReqDTO;

import java.util.Set;

public interface DramaBizService {
    HomeFeaturedDTO getHomeFeature(PageReqDTO pageReqDTO);

    HomeDramaEpisodesDTO getDramaEpisodes(Long dramaId);

    HomeDramaInfoDTO getDramaInfo(Long dramaId);

    Boolean getUserIsLike(String episode);
}

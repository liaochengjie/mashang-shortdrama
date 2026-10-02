package com.lfy.kcat.content.biz;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.dromara.common.core.dto.home.HomeDramaInfoDTO;
import org.dromara.common.core.dto.home.HomeDramaEpisodesDTO;

/**
 * 读取已发布版本的内容视图。
 *
 * @author liaochengjie
 */
public interface RagPublishedViews {
    org.dromara.common.core.dto.home.HomeFeaturedDTO featured(org.dromara.common.core.dto.PageReqDTO req);
    HomeDramaInfoDTO info(Long dramaId);
    HomeDramaEpisodesDTO episodes(Long dramaId);
}

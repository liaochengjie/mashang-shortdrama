package com.lfy.kcat.content.domain.vo;

import com.lfy.kcat.content.domain.Dramas;
import com.lfy.kcat.content.domain.bo.DramasBo;
import com.lfy.kcat.content.domain.bo.EpisodesBo;
import lombok.Data;

import java.util.List;

/**
 * @author 廖成杰
 * @date 2025/11/4
 */
@Data
public class DramaPublishVo {
    private DramasBo drama;
    private List<Long> categories;
    private List<Long> tags;
    private List<PublishActorsVo> actors;
    private List<EpisodesBo> episodes;
}

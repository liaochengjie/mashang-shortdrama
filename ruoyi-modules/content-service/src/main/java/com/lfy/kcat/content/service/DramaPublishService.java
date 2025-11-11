package com.lfy.kcat.content.service;

import com.lfy.kcat.content.domain.vo.DramaPublishVo;
import org.springframework.stereotype.Service;

@Service
public interface DramaPublishService {

    /**
     * 短剧发布
     * @param dramaPublishVo
     * @return
     */
    Long publishDrama(DramaPublishVo dramaPublishVo);
}

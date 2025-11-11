package com.lfy.kcat.content.controller;

import com.lfy.kcat.content.domain.vo.DramaPublishVo;
import com.lfy.kcat.content.service.DramaPublishService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短剧发布
 * 前端录入所有短剧信息，如何一次性录入数据库
 * @author 廖成杰
 * @date 2025/11/3
 */
@RestController
@Slf4j
public class PublishController {


    DramaPublishService DramaPublishService;
    public PublishController(DramaPublishService dramaPublishService) {
        this.DramaPublishService=dramaPublishService;
    }

    @PostMapping("/publish")
    public R publish(@RequestBody DramaPublishVo dramaPublishVo) {
        log.info("短剧发布：内容：{}", dramaPublishVo);
        Long dramaId=DramaPublishService.publishDrama(dramaPublishVo);
        return R.ok();
    }
}

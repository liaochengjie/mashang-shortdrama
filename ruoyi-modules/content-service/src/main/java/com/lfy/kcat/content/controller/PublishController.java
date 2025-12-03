package com.lfy.kcat.content.controller;

import com.lfy.kcat.content.domain.bo.DramasBo;
import com.lfy.kcat.content.domain.vo.DramaPublishVo;
import com.lfy.kcat.content.feign.CamundaFeignClient;
import com.lfy.kcat.content.service.DramaPublishService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CamundaConstants;
import org.dromara.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    CamundaFeignClient camundaFeignClient;

    DramaPublishService DramaPublishService;
    public PublishController(DramaPublishService dramaPublishService) {
        this.DramaPublishService=dramaPublishService;
    }

    @PostMapping("/publish")
    public R publish(@RequestBody DramaPublishVo dramaPublishVo) {
        //1.保存和发布短剧
        log.info("短剧发布：内容：{}", dramaPublishVo);
        Long dramaId=DramaPublishService.publishDrama(dramaPublishVo);


        //2.启动短剧审核


        //保存短剧和审核流对应关系
        return R.ok();
    }
}

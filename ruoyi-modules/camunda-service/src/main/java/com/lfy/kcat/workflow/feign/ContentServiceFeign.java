package com.lfy.kcat.workflow.feign;

import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "content-service")
public interface ContentServiceFeign {
    /**
     * 更新数据库状态
     * @param dramaAuthCompleteDTO
     * @return
     */
    @PutMapping("/dramas/updateAuthDb")
    R updateDramaAuthStatus(@RequestBody DramaAuthCompleteDTO dramaAuthCompleteDTO);

    @PostMapping("/tencent/vod/translate")
    R tencentVodTranslator(@RequestBody DramaAuthCompleteDTO dramaAuthCompleteDTO);
}

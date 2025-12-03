package com.lfy.kcat.content.feign;

import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("camunda-service")
public interface CamundaFeignClient {
    @PostMapping("/process/start/dramaAuth")
    R startDramaAuthProcess(@RequestBody DramaAuthStartDTO dramaAuthStartDTO);
}

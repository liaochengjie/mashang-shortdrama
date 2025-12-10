package com.lfy.kcat.content.feign;

import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthManualTaskDTO;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("camunda-service")
public interface CamundaFeignClient {

    @PutMapping("/process/authtask")
    R claimManualAuthTaskAndComplete(@RequestBody DramaAuthManualTaskDTO dramaAuthManualTaskDTO);


    /**
     * 创建一个流程
     * @param dramaAuthStartDTO
     * @return
     */
    @PostMapping("/process/start/dramaAuth")
    R startDramaAuthProcess(@RequestBody DramaAuthStartDTO dramaAuthStartDTO);
    /**
     * 通过流程ID来获取其变量
     */
    @GetMapping("/process/variables/{processId}")
     R<Map<String,Object>> getProcessVariables(@PathVariable("processId") String processId);
}

package com.lfy.kcat.workflow.Controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.dromara.common.core.constant.CamundaConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController

public class ProcessManagerController {
    @Autowired
    RuntimeService runtimeService;

    /*
      启动流程
     */
    @PostMapping("/process/start/dramaAuth")
    public R startDramaAuthProcess(@RequestBody DramaAuthStartDTO dramaAuthStartDTO) {
        Map<String,Object> map=buildParamMap(dramaAuthStartDTO);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(CamundaConstants.DRAMA_AUTH_PROCESS_DEFINITION_KEY);
        return R.ok("success", processInstance.getId());
    }

    /**
     * 这个方法是用来进行将一个DTO对象转换成hashmap对象的
     * @param dramaAuthStartDTO
     * @return
     */
    private Map<String, Object> buildParamMap(DramaAuthStartDTO dramaAuthStartDTO) {
        Long dramaId = dramaAuthStartDTO.getDramaId();
        String dramaName = dramaAuthStartDTO.getDramaName();
        String description = dramaAuthStartDTO.getDescription();
        HashMap<String, Object> map = new HashMap<>();
        map.put("dramaId", dramaId);
        map.put("dramaName", dramaName);
        map.put("description", description);
        return map;

    }

}

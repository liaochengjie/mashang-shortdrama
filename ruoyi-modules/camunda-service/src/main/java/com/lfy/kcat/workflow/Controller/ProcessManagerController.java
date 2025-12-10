package com.lfy.kcat.workflow.Controller;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.dromara.common.core.constant.CamundaConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.dto.DramaAuthManualTaskDTO;
import org.dromara.common.core.dto.DramaAuthStartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class ProcessManagerController {
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    HistoryService historyService;
    @Autowired
    TaskService taskService;


    /**
     *人工审核任务
     * @param dramaAuthManualTaskDTO
     * @return
     */
    @PutMapping("/process/authtask")
    public R claimManualAuthTaskAndComplete(@RequestBody DramaAuthManualTaskDTO dramaAuthManualTaskDTO) {
        //TODO 人工审核
        log.info("准备执行人工审核:{}",dramaAuthManualTaskDTO);

        //查询任务并且领任务然后完成
        Task task = taskService.createTaskQuery()
                .processInstanceId(dramaAuthManualTaskDTO.getProcessId())
                    .singleResult();
        taskService.claim(task.getId(),dramaAuthManualTaskDTO.getUserName());
        boolean approve=false;
        if("1".equals(dramaAuthManualTaskDTO.getAuditStatus())) {
            approve=true;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("approve", approve);
        map.put("auditReason", dramaAuthManualTaskDTO.getAuditReason());
        map.put("auditStatus", dramaAuthManualTaskDTO.getAuditStatus());

        taskService.complete(task.getId(), map);
        return R.ok("success");


    }

    /**
     * 通过流程ID来获取其变量
     */
    @GetMapping("/process/variables/{processId}")
    public R<Map<String,Object>> getProcessVariables(@PathVariable("processId") String processId) {
        log.info("正在进行历史变量的查询");
        //通过历史服务获取实例的变量
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(processId)
            .list();
        Map<String, Object> map = new HashMap<>();
        for (HistoricVariableInstance historicVariableInstance : list) {
            String name = historicVariableInstance.getName();
            Object value = historicVariableInstance.getValue();
            map.put(name, value);
        }

        return R.ok("success", map);
    }


    /*
      启动流程
     */
    @PostMapping("/process/start/dramaAuth")
    public R startDramaAuthProcess(@RequestBody DramaAuthStartDTO dramaAuthStartDTO) {
        log.info("dramaAuthStartDTO:{}",dramaAuthStartDTO);
        Map<String,Object> map=buildParamMap(dramaAuthStartDTO);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(CamundaConstants.DRAMA_AUTH_PROCESS_DEFINITION_KEY,map);
        log.info("流程ID:{}",processInstance.getId());
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

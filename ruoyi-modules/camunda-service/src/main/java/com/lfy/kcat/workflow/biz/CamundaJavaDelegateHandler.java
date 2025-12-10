package com.lfy.kcat.workflow.biz;

import com.lfy.kcat.workflow.ai.OllamaModerationService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.task.Task;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class CamundaJavaDelegateHandler {

    @Autowired
    TaskService taskService;

    @Autowired
    OllamaModerationService ollamaModerationService;



    public void aiCheck(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        Object dramaId = variables.get("dramaId");
        Object dramaName = variables.get("dramaName");
        Object description = variables.get("description");
        log.info("AI流程审核启动，正在审核{}{}{}", dramaId, dramaName, description);

        //审核名字和短剧简介（名字必为非空，对短剧简介想要非空判断）
        if(!StringUtils.isEmpty(dramaName.toString())) {
            String resultName = ollamaModerationService.moderation(dramaName.toString());
            variables.put("AuthName", resultName);
            log.info("现在审核完毕{},结果为{}",dramaName,resultName);
        }
        if (!StringUtils.isEmpty(description.toString())) {
            String resultDescription = ollamaModerationService.moderation(description.toString());
            variables.put("AuthDescription", resultDescription);
            log.info("现在审核完毕{},结果为{}",description,resultDescription);
        }

        //审核完毕之后将审核结果放入到variables的map中，然后赋给execution
        execution.setVariables(variables);
    }



    public void audiService(DelegateExecution execution) throws InterruptedException {
        System.out.println("audiService 机器自动审核");
        Map<String, Object> variables = execution.getVariables();
        System.out.println("variables:" + variables);
        //机器正在审核
        Thread.sleep(60*1000);
        System.out.println("机器审核完毕");
        String instanceId = execution.getProcessInstanceId();
        Task task = taskService.createTaskQuery()
            .processInstanceId(instanceId)
            .singleResult();

        taskService.complete(task.getId());
    }


    /**
     * 腾讯云转码
     * @param execution
     */
    public void tecentVodTranslator(DelegateExecution execution) {
        log.info("腾讯云转码...");

        Map<String, Object> variables = execution.getVariables();

    }

    /**
     * rag数据入库
     * @param execution
     */
    public void ragDataHandler(DelegateExecution execution) {
        log.info("rag数据入库");
    }
}

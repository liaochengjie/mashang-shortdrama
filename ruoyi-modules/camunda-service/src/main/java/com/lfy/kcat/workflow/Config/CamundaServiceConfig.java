package com.lfy.kcat.workflow.Config;

import com.lfy.kcat.workflow.ai.OllamaModerationService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
@Slf4j
@Configuration
public class CamundaServiceConfig {
    @Autowired
    TaskService taskService;

    @Autowired
    OllamaModerationService ollamaModerationService;
    @Bean("aiCheck")
    public JavaDelegate aiCheck() {
        return new JavaDelegate(){
            @Override
            public void execute(DelegateExecution execution) throws Exception {
                Map<String, Object> variables = execution.getVariables();
                Object dramaId = variables.get("dramaId");
                Object dramaName = variables.get("dramaName");
                Object description = variables.get("description");
                log.info("AI流程审核启动，正在审核{}{}{}", dramaId, dramaName, description);

                //审核名字和短剧简介（名字必为非空，对短剧简介想要非空判断）
                if(StringUtils.isEmpty(dramaName.toString())) {
                    String resultName = ollamaModerationService.moderation(dramaName.toString());
                    variables.put("AuthName", resultName);
                }
                if (StringUtils.isEmpty(description.toString())) {
                    String resultDescription = ollamaModerationService.moderation(description.toString());
                    variables.put("AuthDescription", resultDescription);
                }

                //审核完毕之后将审核结果放入到variables的map中，然后赋给execution
                execution.setVariables(variables);
            }

        };
    }
    public
    @Bean("audiService")
    JavaDelegate audiService() {
        return (execution)->{
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
        };
    }

}

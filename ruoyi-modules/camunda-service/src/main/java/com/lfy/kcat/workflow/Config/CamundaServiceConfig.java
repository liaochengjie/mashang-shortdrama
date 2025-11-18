package com.lfy.kcat.workflow.Config;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CamundaServiceConfig {
    @Autowired
    TaskService taskService;

    @Bean("audiService")
    public JavaDelegate audiService() {
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

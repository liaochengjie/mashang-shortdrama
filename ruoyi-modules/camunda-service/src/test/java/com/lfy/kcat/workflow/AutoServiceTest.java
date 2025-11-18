package com.lfy.kcat.workflow;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class AutoServiceTest {

    @Autowired
    TaskService taskService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    /**
     * 完成任务的启动
     */
    @Test//实例id：fd0a899c-c29f-11f0-877d-00ffd1e9e6d6
    void  test01(){
        ProcessDefinition vocationRequest = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("vocationRequest")
            .latestVersion()
            .singleResult();
        String id = vocationRequest.getId();
        System.out.println("流程id:"+id);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(id);
        System.out.println("实例id:"+processInstance.getId());
    }

    /**
     * 添加入遍历，请假天数以及请假人
     */
    @Test
    void test02(){
        Task task = taskService.createTaskQuery()
            .processInstanceId("fd0a899c-c29f-11f0-877d-00ffd1e9e6d6")
            .active()
            .singleResult();
        System.out.println("实例id："+task.getId());
        System.out.println("实例名称："+task.getName());
        Map<String, Object> variables=new HashMap<>();
        variables.put("username","廖成杰");
        variables.put("vocationNum","3");
        variables.put("reason","想睡觉");
        taskService.setVariables(task.getId(),variables);
        Map<String, Object> variables1 = taskService.getVariables(task.getId());
        System.out.println(variables1);
        System.out.println("请假人："+variables1.get("username"));
        System.out.println("请假天数："+variables1.get("vocationNum"));
        System.out.println("请假原因"+variables1.get("reason"));
        //完成任务
//        taskService.complete(task.getId());

    }
}

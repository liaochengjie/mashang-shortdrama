package com.lfy.kcat.workflow;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProcessTest {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

    @Test
    void test05(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("vocationRequest")
            .list();
        for (ProcessDefinition processDefinition : list) {
            String id = processDefinition.getId();
            System.out.println(id);
        }
    }


    /**
     * 进行查询负责该任务的人，并且让该负责人对其进行完成
     */
    @Test
    void test04(){
        List<Task> list = taskService.createTaskQuery()
            .processInstanceId("a2d85154-c108-11f0-8ac1-00ffd1e9e6d6")
            .list();
        for (Task task : list) {
            System.out.println(task.getName());
            String assignee = task.getAssignee();
            if("廖成杰".equals(assignee)) {
                System.out.println("该任务是廖成杰，下载立即执行完成任务");
                taskService.complete(task.getId());
            }else if (!"陈慷慨".equals(assignee)){
                System.out.println("该任务不是陈慷慨的，现在分配给陈慷慨");
                taskService.claim(task.getId(),"陈慷慨");
            }else{
                taskService.complete(task.getId());
            }

        }
    }


    /**
     * 查看该任务为什么任务 &并且对该任务进行分配负责人
     */
    @Test
    void test03(){
        List<Task> list = taskService.createTaskQuery()
            .processInstanceId("a2d85154-c108-11f0-8ac1-00ffd1e9e6d6")
            .list();

        for (Task task : list) {
            System.out.println("当前任务是:"+task.getName());
            String id = task.getId();
            taskService.claim(id,"廖成杰");

            System.out.println(id);
        }
    }


    /**
     * 获取所有的任务实例
     */
    @Test
    void test02(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
            .processDefinitionKey("vocationRequest")
            .list();
        for (ProcessInstance processInstance : list) {
            System.out.println("实例:"+processInstance.getId());
        }
    }

    @Test
    void test01(){
        System.out.println(processEngine);
    }

}

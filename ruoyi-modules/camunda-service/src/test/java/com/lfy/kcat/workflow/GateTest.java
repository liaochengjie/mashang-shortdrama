package com.lfy.kcat.workflow;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class GateTest {
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;
    /**
     * 添加实例对象
     */
    @Test
    void test01(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("vocation2:1:9db24bdd-c5dc-11f0-a4cb-00ffd1e9e6d6");
        String Id = processInstance.getId();
        System.out.println("ID："+Id);
        System.out.println("实例创建完成");

    }
    //46976885-c5dc-11f0-bbb2-00ffd1e9e6d6

    @Test
    void test02(){
        Task task = taskService.createTaskQuery()
            .processInstanceId("b407c3b7-c5dc-11f0-832b-00ffd1e9e6d6")
            .singleResult();
        taskService.claim(task.getId(), "leifengyang");
        Map<String, Object> stringObjectMap = new HashMap<String,Object>();
        stringObjectMap.put("day",1);
        taskService.complete(task.getId(),stringObjectMap);
    }


    @Test
    void test03(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("vocation1:2:8964789e-c5de-11f0-8e37-00ffd1e9e6d6");
        String id = processInstance.getId();
        System.out.println("id:"+id);

    }


    //e386823c-c5de-11f0-9a78-00ffd1e9e6d6
    @Test
    void test04(){
        List<Task> list = taskService.createTaskQuery()
            .processInstanceId("e386823c-c5de-11f0-9a78-00ffd1e9e6d6")
            .list();
        for (Task task : list) {
            taskService.claim(task.getId(), "leifengyang");
            taskService.complete(task.getId());
        }
    }

    @Test
    void test05(){
        List<Task> list = taskService.createTaskQuery()
            .processInstanceId("e386823c-c5de-11f0-9a78-00ffd1e9e6d6")
            .list();
        for (Task task : list) {
            taskService.claim(task.getId(), "leifengyang");
            System.out.println(task.getName());
            taskService.complete(task.getId());
        }
    }
}

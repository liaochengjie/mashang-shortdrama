package com.lfy.kcat.workflow.Controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class ProcessFunctionController {

    @Autowired
    RuntimeService runtimeService;

    /**
     *
     * @return
     */
    @RequestMapping("/vation/process/start")
    public List<String> getByKey(){
        ProcessInstance vocationRequest = runtimeService.startProcessInstanceByKey("vocationRequest");
        String businessKey = vocationRequest.getBusinessKey();
        String id = vocationRequest.getId();

        return   Arrays.asList(id,businessKey);
    }
}

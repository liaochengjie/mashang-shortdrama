package com.lfy.kcat.workflow.Controller;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.dromara.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/workflow")
public class ProcessDeployController {
    @Autowired
    RepositoryService repositoryService;


    @PostMapping("deploy")
    public R deployProcess(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        Deployment deploy = repositoryService.createDeployment()
            .addInputStream(filename, inputStream)
            .deploy();

        //返回流程定义id
        return R.ok("success", deploy.getId());
    }
}

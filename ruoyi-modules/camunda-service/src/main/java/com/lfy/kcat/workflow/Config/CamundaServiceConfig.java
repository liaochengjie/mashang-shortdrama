package com.lfy.kcat.workflow.Config;

import com.lfy.kcat.workflow.ai.OllamaModerationService;
import com.lfy.kcat.workflow.biz.CamundaJavaDelegateHandler;
import com.lfy.kcat.workflow.feign.ContentServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.dromara.common.core.dto.DramaAuthCompleteDTO;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
@Slf4j
@Configuration
public class CamundaServiceConfig {
    @Autowired
    CamundaJavaDelegateHandler  camundaJavaDelegateHandler;

    @Autowired
    ContentServiceFeign contentServiceFeign;

    private DramaAuthCompleteDTO buildCompleteDTO(Map<String, Object> variables) {
        System.out.println(variables);
        DramaAuthCompleteDTO dramaAuthCompleteDTO = new DramaAuthCompleteDTO();
        dramaAuthCompleteDTO.setDramaId((Long) variables.get("dramaId"));
        Object auditReason = variables.get("auditReason");
        if (auditReason != null) {
            dramaAuthCompleteDTO.setAuditReason(auditReason.toString());
        }

        dramaAuthCompleteDTO.setAuditStatus((String) variables.get("auditStatus"));
        dramaAuthCompleteDTO.setApprove((Boolean) variables.get("approve"));
        dramaAuthCompleteDTO.setAuthName((String) variables.get("authName" ));
        dramaAuthCompleteDTO.setAuthStatus((String) variables.get("authStatus"));
        dramaAuthCompleteDTO.setAuthName((String) variables.get("authName" ));
        dramaAuthCompleteDTO.setAuthStatus((String) variables.get("authStatus"));
        System.out.println(dramaAuthCompleteDTO);
        return dramaAuthCompleteDTO;
    }
    /**
     * 更新数据库状态
     * @return
     */
    @Bean("updateDramaAuthStatus")
    public JavaDelegate updateDramaAuthStatus(){
        return (execution)->{
            Map<String, Object> variables = execution.getVariables();
            log.info("审核短剧信息为:{}",variables);
            DramaAuthCompleteDTO dramaAuthCompleteDTO=buildCompleteDTO(variables);
            contentServiceFeign.updateDramaAuthStatus(dramaAuthCompleteDTO);
        };
    }



    /**
     * rag数据入库
     * @return
     */
    @Bean("ragDataHandler")
    public JavaDelegate ragDataHandler(){
        return (execution)->{
            camundaJavaDelegateHandler.ragDataHandler(execution);
        };
    }


    /**
     * 腾讯云转码
     * @return
     */
    @Bean("tecentVodTranslator")
    public JavaDelegate tecentVodTranslator(){
        return (execution)->{
            camundaJavaDelegateHandler.tecentVodTranslator(execution);
        };
    }
    /**
     * AI审核代理
     * @return
     */
    @Bean("aiCheck")
    public JavaDelegate aiCheck() {
        return new JavaDelegate(){
            @Override
            public void execute(DelegateExecution execution) throws Exception {
                camundaJavaDelegateHandler.aiCheck(execution);

            }

        };
    }
    public
    @Bean("audiService")
    JavaDelegate audiService() {
        return (execution)->{
           camundaJavaDelegateHandler.audiService(execution);
        };
    }

}

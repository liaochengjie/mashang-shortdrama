package com.lfy.kcat.workflow.Config;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;

/**
 * @author liaochengjie
 */
@Configuration
public class RagProcessDelegates {
    @Value("${rag.content-url:http://127.0.0.1:10001}") private String contentUrl;
    @Value("${rag.content-token:}") private String contentToken;

    private Map call(DelegateExecution execution, String action, boolean post) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); factory.setReadTimeout(15000);
        RestClient client = RestClient.builder().baseUrl(contentUrl).requestFactory(factory).defaultHeader("Authorization", "Bearer " + contentToken).build();
        String path = "/internal/rag/snapshots/" + execution.getVariable("snapshotId") + action;
        try {
            return post ? client.post().uri(path).body(Map.of("approve", Boolean.TRUE.equals(execution.getVariable("approve")))).retrieve().body(Map.class)
                : client.get().uri(path).retrieve().body(Map.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 409 && e.getResponseBodyAsString().contains("SUPERSEDED")) throw new BpmnError("SUPERSEDED");
            throw new IllegalStateException("CONTENT_SERVICE_HTTP_" + e.getStatusCode().value());
        }
    }

    @Bean("ragStartMedia") public JavaDelegate startMedia() { return e -> call(e,"/media",true); }
    @Bean("ragPollMedia") public JavaDelegate pollMedia() {
        return e -> {
            Map state = call(e,"/media",false);
            if ("FAILED".equals(state.get("state"))) throw new IllegalStateException("MEDIA_FAILED_REQUIRES_OPERATOR");
            e.setVariable("mediaReady", "READY".equals(state.get("state")));
        };
    }
    @Bean("ragPublish") public JavaDelegate publish() { return e -> call(e,"/finish",true); }
}

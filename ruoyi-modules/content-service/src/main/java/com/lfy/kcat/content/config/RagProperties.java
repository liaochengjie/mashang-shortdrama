package com.lfy.kcat.content.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liaochengjie
 */
@Data
@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {
    private boolean enabled = false;
    private String internalToken = "";
    private String camundaToken = "";
    private String camundaUrl = "http://127.0.0.1:8088";
    private String pipelineVersion = "evidence-v1";
    private String embeddingProfile = "";
    private String mediaAllowedHosts = "localhost,127.0.0.1,minio";
}

package com.lfy.kcat.content.vod.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@ConfigurationProperties(prefix = "vod")
@Component
@Data
public class VodProperties {
    private String secretId;
    private String secretKey;
    private Long stuAppId;
    private String region;

}

package com.lfy.kcat.content.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my-minio")
@Data
public class MyMinioProperties {
    //访问地址
    private String endpoint;
    private String accessKey;

    private  String secretKey;
}

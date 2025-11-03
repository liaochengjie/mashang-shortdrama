package com.lfy.kcat.content.minio.config;


import com.lfy.kcat.content.minio.properties.MyMinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyMinioConfig {

    @Autowired
    private MyMinioProperties myMinioProperties;

    @Bean
    MinioClient minioClient() {

        MinioClient client = MinioClient.builder()
            .endpoint(myMinioProperties.getEndpoint())
            .credentials(myMinioProperties.getAccessKey(), myMinioProperties.getSecretKey())
            .build();
        return client;
    }
}

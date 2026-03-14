package com.lfy.kcat.demokafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AppConfig {

    @Bean
    NewTopic topic2(){
        NewTopic topic2 = TopicBuilder.name("topic2")
            .partitions(3)
            .replicas(1)
            .build();
        return topic2;
    }
}

package com.lfy.kcat.user.config;

import com.lfy.kcat.user.interceptor.KafkaUserEventProducerInterceptor;
import org.apache.kafka.clients.admin.NewTopic;
import org.dromara.common.core.constant.KafkaConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic likeTopic(){
        return TopicBuilder.name(KafkaConstant.LIKE_EVENT_TOPIC)
            .partitions(10)
            .replicas(1)
            .build();
    }

    @Bean
    public KafkaUserEventProducerInterceptor kafkaUserEventProducerInterceptor() {
        return new KafkaUserEventProducerInterceptor();
    }
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> pf, KafkaUserEventProducerInterceptor kafkaUserEventProducerInterceptor) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(pf);
        kafkaTemplate.setProducerInterceptor(kafkaUserEventProducerInterceptor);
        return kafkaTemplate;
    }
}

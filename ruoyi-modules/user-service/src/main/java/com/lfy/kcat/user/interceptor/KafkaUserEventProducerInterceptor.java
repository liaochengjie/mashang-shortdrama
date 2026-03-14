package com.lfy.kcat.user.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.dromara.common.core.dto.evert.BaseEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KafkaUserEventProducerInterceptor implements ProducerInterceptor<String, Object> {
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> producerRecord) {
        log.info("kafka拦截器, 发送消息: {}", producerRecord);
        Object value = producerRecord.value();
        if(value instanceof BaseEvent){
            BaseEvent baseEvent = (BaseEvent) value;
            //雪花算法
            long snowflakeNextId = IdUtil.getSnowflakeNextId();
            baseEvent.setEventId(snowflakeNextId);
            boolean login = StpUtil.isLogin();
            if(login) {
                Long loginId = Long.parseLong(StpUtil.getLoginId().toString());
                baseEvent.setUserId(Long.parseLong(loginId.toString()));
            }else{
                log.info("未登录用户设置统一的userId");
                baseEvent.setUserId(0L);
            }
            baseEvent.setCurrentTimeMillis(System.currentTimeMillis());
        }

        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        log.info("kafka拦截器, 发送消息确认: {}", recordMetadata);
        log.info("消息确认发送到分区: {}", recordMetadata.partition());
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}

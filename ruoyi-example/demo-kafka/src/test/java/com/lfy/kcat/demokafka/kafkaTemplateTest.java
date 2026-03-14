package com.lfy.kcat.demokafka;

import com.lfy.kcat.demokafka.controller.KafkaTestController;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
public class kafkaTemplateTest {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    void test01() throws ExecutionException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        String[] split = uuid.split("_");
        String key=split[0];
        String value=split[1];
        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send("topic2", key, value);
        SendResult<String, String> sendResult = completableFuture.get();
        ProducerRecord<String, String> producerRecord = sendResult.getProducerRecord();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        String.format("topic:%s,partition:%s,offset:%s,key:%s,value:%s",
            producerRecord.topic(),
            producerRecord.partition(),
            recordMetadata.offset(),
            producerRecord.key(),
            producerRecord.value());
        System.out.println(String.format("topic:%s,partition:%s,offset:%s,key:%s,value:%s",
            producerRecord.topic(),
            producerRecord.partition(),
            recordMetadata.offset(),
            producerRecord.key(),
            producerRecord.value()));

    }

}

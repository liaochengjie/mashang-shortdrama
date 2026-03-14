package com.lfy.kcat.demokafka.controller;

import jakarta.websocket.server.PathParam;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class KafkaTestController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @GetMapping("/kafka/send")
    public String send() throws ExecutionException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        String[] split = uuid.split("-");
        String key = split[0];
        String value = split[1];
        CompletableFuture<SendResult<String, String>> resultCompletableFuture = kafkaTemplate.send("topic2", key, value);
        SendResult<String, String> stringStringSendResult = resultCompletableFuture.get();
        ProducerRecord<String, String> producerRecord =
            stringStringSendResult.getProducerRecord();
        RecordMetadata recordMetadata = stringStringSendResult.getRecordMetadata();
        String.format("topic:%s,partition:%s,offset:%s,key:%s,value:%s",
                producerRecord.topic(),
                producerRecord.partition(),
                recordMetadata.offset(),
                producerRecord.key(),
                producerRecord.value());
        return "success";
    }

}

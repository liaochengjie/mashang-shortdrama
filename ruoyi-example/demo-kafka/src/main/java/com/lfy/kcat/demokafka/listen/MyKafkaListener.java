package com.lfy.kcat.demokafka.listen;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class MyKafkaListener {

    @KafkaListener(topics = "topic1")
    public void listen01(String msg) {
        System.out.println("listen01:" + msg);
    }


    @KafkaListener(topicPartitions=@TopicPartition(topic = "topic2",
    partitionOffsets = {
            @PartitionOffset(partition = "0", initialOffset = "0"),
            @PartitionOffset(partition = "1", initialOffset = "0"),
            @PartitionOffset(partition = "2", initialOffset = "0")
    }))
    public void listen02(ConsumerRecord<String, Object> record) {
        System.out.println(String.format("topic:%s,partition:%s,offset:%s,key:%s,value:%s",
                record.topic(),record.partition(),record.offset(),record.key(),record.value()));

    }
}

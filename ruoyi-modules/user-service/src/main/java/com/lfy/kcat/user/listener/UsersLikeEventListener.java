package com.lfy.kcat.user.listener;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.dromara.common.core.constant.KafkaConstant;
import org.dromara.common.core.dto.evert.LikeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsersLikeEventListener {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @KafkaListener(topics = KafkaConstant.LIKE_EVENT_TOPIC)
    public void listenUsersLikeEvent(ConsumerRecord<String,Object> consumerRecord,
                                     Acknowledgment ack){
        try {
            //1.获取消息
            Object value = consumerRecord.value();
            LikeEvent likeEvent = JSONUtil.toBean((String) value, LikeEvent.class);
            //2.处理消息
            String action = likeEvent.getAction();
            if (action.equals("like")) {
                stringRedisTemplate.opsForSet().add("likes" + likeEvent.getEpisodeId(), likeEvent.getUserId().toString());
            } else {
                stringRedisTemplate.opsForSet().remove("likes" + likeEvent.getEpisodeId(), likeEvent.getUserId().toString());
            }
            log.info("处理点赞事件: {}", likeEvent);
            //3.确认消息
            ack.acknowledge();
            log.info("userService处理点赞事件成功: {}", likeEvent);
        }catch (Exception e){
            log.error("处理点赞事件失败: {}", e.getMessage(), e);
        }
    }
}

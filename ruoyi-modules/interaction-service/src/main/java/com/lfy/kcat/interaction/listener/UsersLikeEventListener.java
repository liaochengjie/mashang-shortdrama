package com.lfy.kcat.interaction.listener;


import cn.hutool.json.JSONUtil;
import com.lfy.kcat.interaction.business.UsersLikeService;
import com.lfy.kcat.interaction.constant.RedisFromUserServiceConst;
import com.lfy.kcat.interaction.template.KafkaBloomFilterTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.dromara.common.core.constant.KafkaConstant;
import org.dromara.common.core.dto.action.UsersLikeDTO;
import org.dromara.common.core.dto.evert.LikeEvent;
import org.dromara.common.core.dto.home.HomeFeaturedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UsersLikeEventListener {

    @Autowired
    KafkaBloomFilterTemplate kafkaBloomFilterTemplate;
    @Autowired
    UsersLikeService usersLikeService;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @KafkaListener(topics = KafkaConstant.LIKE_EVENT_TOPIC, groupId = "interaction-service")
    public void likeEventListener(ConsumerRecord<String,Object> record,
                                  Acknowledgment ack){
        log.info("收到点赞事件: {}", record.value());
        Object value = record.value();
        try {
            //1.将json传解析
            LikeEvent likeEvent = JSONUtil.toBean((String) value, LikeEvent.class);
            Long eventId = likeEvent.getEventId();
            //保证幂等性，先判断是否存在该事件
            //2.通过布隆过滤器判断是否重复
            boolean contains = kafkaBloomFilterTemplate.checkEventIsExist(eventId);
            if (contains){
                //2.1如果已经存在那么已经消费该事件，直接ack
                log.info("点赞事件重复: {}", record.value());
                ack.acknowledge();
            }
            //2.2如果未存在该事件，那么加入到布隆过滤器中，继续消费
            //3.将解析出来的数据封装到UsersLikeDTO
            UsersLikeDTO usersLikeDTO = new UsersLikeDTO();
            usersLikeDTO.setUserId(likeEvent.getUserId());
            usersLikeDTO.setEpisodeId(likeEvent.getEpisodeId());
            usersLikeDTO.setLike(likeEvent.getAction());
            //调用点赞接口，更新用户在数据库点赞状态
            usersLikeService.likeEpisode(usersLikeDTO);
            //获取首页精彩数据缓存
            String homeFeatureCache = stringRedisTemplate.opsForValue().get("home:feature:1:15");
            HomeFeaturedDTO homeFeaturedDTO = JSONUtil.toBean(homeFeatureCache, HomeFeaturedDTO.class);
            for (HomeFeaturedDTO.EpisodesDTO episode : homeFeaturedDTO.getEpisodes()) {
                if (episode.getEpisode().equals(likeEvent.getEpisodeId().toString())){
                    Integer likeCount = episode.getLikeCount();
                    Integer newLikeCount = likeCount + (likeEvent.getAction().equals("like")?1:-1);
                    episode.setLikeCount(newLikeCount);
//                    if(newLikeCount>likeCount){
//                        episode.setIsLiked(true);
//                    }else if(newLikeCount<likeCount){
//                        episode.setIsLiked(false);
//                    }
                }
            }
            String newHomeFeatureCache = JSONUtil.toJsonStr(homeFeaturedDTO);
            //4.更新缓存
            stringRedisTemplate.opsForValue().set("home:feature:1:15", newHomeFeatureCache);

            //记入布隆过滤器，防止重复消费
            kafkaBloomFilterTemplate.addEventToBloomFilter(eventId);
            //6.手动ack
            ack.acknowledge();

            log.info("点赞事件成功: {}", record.value());
        }catch (Exception e){
            log.error("点赞事件失败: {}", record.value(), e);
        }



    }
}

package com.lfy.kcat.interaction.template;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.KafkaConstant;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaBloomFilterTemplate {
    @Autowired
    RedissonClient redissonClient;
    //初始化布隆过滤器

    @Bean
    public RBloomFilter<String> likeEventBloomFilter(RedissonClient redissonClient) {
        // 获取布隆过滤器实例
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(KafkaConstant.LIKE_KAFKA_BF);

        // 初始化布隆过滤器
        // 参数1: 预计插入的元素数量
        // 参数2: 误判率 (0.01表示1%的误判率)
        bloomFilter.tryInit(1000000L, 0.01);

        return bloomFilter;
    }

    public boolean checkEventIsExist(Long eventId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(KafkaConstant.LIKE_KAFKA_BF);
        boolean contains = bloomFilter.contains(eventId);
        return contains;

    }

    public void addEventToBloomFilter(Long eventId) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(KafkaConstant.LIKE_KAFKA_BF);
        bloomFilter.add(eventId);
    }
}

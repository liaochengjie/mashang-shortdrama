package com.lfy.kcat.content;

import com.lfy.kcat.content.biz.BloomFilterTemplate;
import com.lfy.kcat.content.config.RedisConst;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class RedissonTest {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    BloomFilterTemplate bloomFilterTemplate;
    @Test
    void RedissonTest01(){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF);
        boolean contains = bloomFilter.contains(111L);
        System.out.println(contains);
    }

    @Test
    void RedissonTest02(){
        boolean result = bloomFilterTemplate.checkDramaIdBloomFilter(2018626608650907650L);
        System.out.println(result);
    }
}

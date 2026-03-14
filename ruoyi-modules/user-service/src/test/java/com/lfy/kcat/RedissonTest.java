package com.lfy.kcat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@Slf4j
public class RedissonTest {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void testBitMap(){
        stringRedisTemplate.opsForValue().setBit("bitmap",100,true);
        stringRedisTemplate.opsForValue().setBit("bitmap",101,true);
        stringRedisTemplate.opsForValue().setBit("bitmap",102,true);
        log.info("位图数据添加成功");
        Boolean bit100 = stringRedisTemplate.opsForValue().getBit("bitmap",100);
        Boolean bit101 = stringRedisTemplate.opsForValue().getBit("bitmap",101);
        Boolean bit999 = stringRedisTemplate.opsForValue().getBit("bitmap", 999);
        System.out.println(bit100);
        System.out.println(bit101);
        System.out.println(bit999);
        log.info("位图进行查询结束" );

    }


    /**
     * 测试布隆过滤器
     */
    @Test
    void testAddBloom(){
        RBloomFilter<Object> myBloomFilter = redissonClient.getBloomFilter("myBloomFilter");
        // 初始化布隆过滤器，预计插入1000000条数据，期望错误率0.01
        myBloomFilter.tryInit(1000000, 0.01);

        myBloomFilter.add("www.baidu.com");
        myBloomFilter.add("www.qianwen.com");
        System.out.println(myBloomFilter.contains("www.baidu.com"));
        System.out.println(myBloomFilter.contains("www.app.com"));
    }
}

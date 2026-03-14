package com.lfy.kcat.content;


import com.lfy.kcat.content.biz.BloomFilterTemplate;
import io.minio.MinioClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest

public class MinioClientTest {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    BloomFilterTemplate bloomFilterTemplate;
    @Test
    public void ClientTest(){

        Assertions.assertNotNull(minioClient);
    }

    @Test
    public void uploadTest1(){
        System.out.println("aaa");
    }

    @Test
    void RedissonTest02(){
        boolean result = bloomFilterTemplate.checkDramaIdBloomFilter(2018626608650907650L);
        System.out.println(result);
    }
}

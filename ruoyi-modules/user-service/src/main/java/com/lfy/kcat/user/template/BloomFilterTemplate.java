package com.lfy.kcat.user.template;

import com.lfy.kcat.user.constant.RedisConst;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BloomFilterTemplate {

    @Autowired
    RedissonClient redissonClient;
    public void initBloomFilter(){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF);
        boolean init = bloomFilter.tryInit(1000000, 0.001);
        if (init){
            log.info("初始化布隆过滤器成功");
        }else {
            log.error("布隆过滤器已经初始化过了，无需初始化");
        }
    }
    public  boolean checkDramaIsExist(Long dramaId){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF);
        boolean contains = bloomFilter.contains(dramaId);
        return  contains;
    }
}

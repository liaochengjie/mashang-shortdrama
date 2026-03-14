package com.lfy.kcat.content.biz;


import com.lfy.kcat.content.config.RedisConst;
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


    public void addDramaIdBloomFilter(Long dramaId){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF);
        bloomFilter.add(dramaId);
    }

    public boolean checkDramaIdBloomFilter(Long dramaId){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConst.DRAMA_BF);
        boolean contains = bloomFilter.contains(dramaId);
        return contains;
    }
}

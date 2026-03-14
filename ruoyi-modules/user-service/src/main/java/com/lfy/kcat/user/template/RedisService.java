package com.lfy.kcat.user.template;

import cn.hutool.core.util.RandomUtil;
import com.lfy.kcat.user.constant.RedisConst;
import com.lfy.kcat.user.feign.ContentServiceFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ContentServiceFeignClient contentServiceFeignClient;

    /**
     * 从Redis中获取缓存数据
     * @param cacheKey
     * @param tClass
     * @return
     * @param <T>
     */
    public <T> T getData(String cacheKey,Class<T> tClass){
        String json = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isEmpty(json)){
            return null;
        }
        if (RedisConst.NULL_VALUE.equals(json)){
            return null;
        }
        return JsonUtils.parseObject(json,tClass);
    }

    public boolean dataIsNullValue(String cacheKey){
        String json = redisTemplate.opsForValue().get(cacheKey);
        if(RedisConst.NULL_VALUE.equals(json)){
            return true;
        }
        return false;
    }

    /**
     * 保存数据（默认时间为3天）
     * 用秒为单位
     * @param cacheKey
     * @param data
     *
     */
    public void saveData(String cacheKey,Object data){
        //添加随机时间，防止缓存雪崩
        String randomNumbers = RandomUtil.randomNumbers(5);
        long randomTime = Long.parseLong(randomNumbers);
        if (data==null) {
            redisTemplate.opsForValue().set(cacheKey, RedisConst.NULL_VALUE, RedisConst.NULL_VALUE_TIMEOUT+randomTime, TimeUnit.SECONDS);
            return;
        }
        String jsonString = JsonUtils.toJsonString(data);
        redisTemplate.opsForValue().set(cacheKey, jsonString, RedisConst.DEFAULT_TIMEOUT+randomTime, TimeUnit.SECONDS);
        log.info("cacheKey:{}保存缓存数据成功，缓存时间为{}秒",cacheKey,RedisConst.DEFAULT_TIMEOUT);
    }

    /**
     * 保存数据（自定义时间）
     * 用秒为单位
     * @param cacheKey
     * @param data
     * @param timeout
     */
    public void saveData(String cacheKey,Object data,Long timeout){
        //添加随机时间，防止缓存雪崩
        String randomNumbers = RandomUtil.randomNumbers(5);
        long randomTime = Long.parseLong(randomNumbers);
        //防止缓存穿透
        if (data==null){
            redisTemplate.opsForValue().set(cacheKey, RedisConst.NULL_VALUE, RedisConst.NULL_VALUE_TIMEOUT+randomTime, TimeUnit.SECONDS);
            return;
        }
        String jsonString = JsonUtils.toJsonString(data);
        redisTemplate.opsForValue().set(cacheKey,jsonString,timeout+randomTime,TimeUnit.SECONDS);
        log.info("cacheKey:{}保存缓存数据成功，缓存时间为{}秒",cacheKey,timeout);
    }

    public String lock(String lockKey) throws InterruptedException {
        String tokenValue = UUID.randomUUID().toString();
        log.info("正在进行incr操作");

        // 加锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, tokenValue ,30, TimeUnit.SECONDS);
        while(!lock){
            TimeUnit.MICROSECONDS.sleep(50);
            //如果为trus，说明抢锁成功
            lock = redisTemplate.opsForValue().setIfAbsent(lockKey, tokenValue ,30, TimeUnit.SECONDS);
        }
        return tokenValue;
    }

    public void unlock(String lockKey,String tokenValue){
        String script="if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
            "then\n" +
            "    return redis.call(\"del\",KEYS[1])\n" +
            "else\n" +
            "    return 0\n" +
            "end";
        DefaultRedisScript<Long> longDefaultRedisScript = new DefaultRedisScript<>(script, Long.class);
        Long lockresult = redisTemplate.execute(longDefaultRedisScript, Arrays.asList("lock"), tokenValue);
        if(lockresult == 0){
            log.info("差点删除别人的锁");
        }else{
            log.info("锁删除成功");
        }
    }



}

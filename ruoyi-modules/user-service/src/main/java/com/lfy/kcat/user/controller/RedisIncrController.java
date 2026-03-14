package com.lfy.kcat.user.controller;

import com.lfy.kcat.user.template.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/api")
public class RedisIncrController {

    private String LOCK_KEY = "lock";
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisService redisService;
    @GetMapping("/incr")
    public String incr() throws InterruptedException {

        //加锁
        String tokenValue = redisService.lock(LOCK_KEY);

        // 从redis中获取incrNum
        String incrNum = redisTemplate.opsForValue().get("incrNum");
        int incr = Integer.parseInt(incrNum);
        incr++;
        //保存incrNum
        redisTemplate.opsForValue().set("incrNum",incr+"");

        //解锁
        redisService.unlock(LOCK_KEY,tokenValue);

        //可能由于网络IO导致错误
//        //获取当前锁的值
//        String lockValue = redisTemplate.opsForValue().get("lock");
//        //解锁
//        //判断当前锁是否为自己设置的（代码业务逻辑过长导致超过过期时间，从而自动删锁）
//        if (uuid.equals(lockValue)){
//            redisTemplate.delete("lock");
//        }
        log.info("incr操作完成");
        return "ok";
    }
}

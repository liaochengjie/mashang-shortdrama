package com.lfy.kcat.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class RedissonTestController {
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/read")
    public String testReadWriterLockRead() throws InterruptedException {
        RReadWriteLock readRriterLock = redissonClient.getReadWriteLock("readRriterLock");
        RLock rLock = readRriterLock.readLock();

        rLock.lock();
        log.info("readLock 加锁成功");
        Thread.sleep(10000);
        rLock.unlock();
        log.info("readLock 解锁成功");
        return "testReadSuccess";
    }

    @GetMapping("/write")
    public String testReadWriterLockWrite() throws InterruptedException {
        RReadWriteLock readRriterLock = redissonClient.getReadWriteLock("readRriterLock");
        RLock wLock = readRriterLock.writeLock();
        wLock.lock();
        log.info("writeLock 加锁成功");
        Thread.sleep(10000);
        wLock.unlock();
        log.info("writeLock 解锁成功");
        return "testWriteSuccess";
    }

    @GetMapping("/redisson/testLock")
    public String testLock() throws InterruptedException {
        log.info("业务执行开始");
        RLock redissonLock = redissonClient.getLock("redissonLock");
        try {
            log.info("准备上锁");
            redissonLock.lock();

        }finally {
            Thread.sleep(10000);
            redissonLock.unlock();
            log.info("解锁成功");
        }
        log.info("业务执行结束");
        return "test lock success";
    }

    @GetMapping("/redisson/testLimiter")
    public String testLimiter(){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("rateLimiter");
        boolean acquire = rateLimiter.tryAcquire();
        if(acquire){
            return "acquire success";
        }else{
            return "acquire failed";
        }
    }


    @GetMapping("/redisson/init")
    public String initLimiter(){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("rateLimiter");
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.MINUTES);
        return "init limiter success";
    }
}

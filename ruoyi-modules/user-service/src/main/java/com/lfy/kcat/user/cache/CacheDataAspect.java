package com.lfy.kcat.user.cache;

import com.lfy.kcat.user.template.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
@Aspect
@Slf4j
public class CacheDataAspect {

    @Autowired
    RedisService redisService;

    @Autowired
    RedissonClient redissonClient;
    @Around("@annotation(com.lfy.kcat.user.cache.CacheData)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("环绕通知开始");
        //获取注解中的值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CacheData cacheDataAnn = signature.getMethod().getDeclaredAnnotation(CacheData.class);
        String cacheKey = cacheDataAnn.cacheKey();
        //方法参数
        Object[] args = joinPoint.getArgs();
        //拼装完整的缓存key
        cacheKey=cacheKey+args[0];
        //得到所需要返回的对象名称
        Class returnType = signature.getReturnType();
        Object data = redisService.getData(cacheKey, returnType);
        //如果缓存不等于空，那么直接返回数据
        if(data!=null) {
            log.info("缓存命中");
            return data;
        }
        log.info("缓存未命中，现在查询布隆过滤器是否包含");
        String bloomFilterName = cacheDataAnn.bloomFilterName();
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
        boolean contains = bloomFilter.contains(args[0]);
        if (!contains) {
            log.info("布隆过滤器未存在该元素，返回null");
            return null;
        }
        log.info("布隆过滤器存在该元素，现在尝试获取锁");
        String lockName="lock:"+cacheKey;
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean lockResult = lock.tryLock();
            if (lockResult) {
                log.info("抢锁成功进行回源数据查询");
                //执行原方法，获取数据
                Object proceed = joinPoint.proceed();
                //回源数据查询成功后，将数据保存到缓存中
                redisService.saveData(cacheKey, proceed);

                return proceed;
            } else {
                log.info("抢锁失败，等待两秒钟后获取缓存数据");
                Thread.sleep(2000);
                data = redisService.getData(cacheKey, returnType);
            }
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("解锁成功");
            }
        }
        log.info("环绕通知结束");
        return data;
    }
}

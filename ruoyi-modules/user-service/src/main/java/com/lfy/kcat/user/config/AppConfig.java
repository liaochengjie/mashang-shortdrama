package com.lfy.kcat.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {
    @Value("${app.executor.corePoolSize}")
    private int corePoolSize;
    @Value("${app.executor.maxPoolSize}")
    private int maxPoolSize;
    @Value("${app.executor.keepAliveSeconds}")
    private int keepAliveSeconds;
    @Value("${app.executor.queueCapacity}")
    private int queueCapacity;


    @Bean("appThreadPoolExecutor")
    public ThreadPoolExecutor executor() {
        //配置线程池为核心线程数为10，最大线程数为20，线程keep-alive时间为60秒，队列容量为500，拒绝策略为CallerRunsPolicy
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,
            maxPoolSize,
            keepAliveSeconds,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}

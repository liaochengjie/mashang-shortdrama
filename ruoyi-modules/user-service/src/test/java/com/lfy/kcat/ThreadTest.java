package com.lfy.kcat;

import io.reactivex.rxjava3.core.Completable;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;


public class ThreadTest {

    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        4,
        8,
        60,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(100)
    );


    @Test
    void testCompletableAllof(){
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "任务完成");
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "任务完成");
        });
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "任务完成");
        });

        CompletableFuture.anyOf(future1,future2,future3).join();

        System.out.println("所有任务完成");
    }

    /**
     * 测试CompletableFuture
     */
    @Test
    void testCompletableFuture() {
        CompletableFuture
            .supplyAsync(() -> "hello")
            .thenApply(res -> res + ":world")
            .thenAcceptAsync(res -> System.out.println(Thread.currentThread().getName() + res))
            .thenRun(()-> System.out.println(Thread.currentThread().getName()+"任务完成"));

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "hello");
        });

        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "haha");
            return "ok";
        });
        System.out.println(supplyAsync.get());
        Thread.sleep(50000);
    }


    //测试线程池
    @Test
    public void testThread() {
        System.out.println("现在开始进行线程测试");
        executor.submit(() -> {

            while (true) {
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + "hello");
            }
        });
        executor.submit(() -> {

            while (true) {
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + "haha");
            }
        });
        executor.submit(() -> {

            while (true) {
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + "yeal");
            }
        });

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

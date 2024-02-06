package com.atguigu.dga;

/**
 * @date 2024-02-05
 */
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
        CompletableFuture: 1.8之后提供，功能强大
 */
@SpringBootTest
public class TaskTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ThreadPoolTaskExecutor pool;
    @Test
    public void test(){

        //计时器
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CompletableFuture [] result = new CompletableFuture[10];
        /*
            CompletableFuture.supplyAsync(): 向线程池提交一个任务，至于任务什么时候完成，你是无法控制。
                这个任务总会在未来的某个时刻完成。
         */
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            result[i] = CompletableFuture
                    //异步提交
                    .supplyAsync(new Supplier<String>() {
                        //编写要执行的任务逻辑
                        @Override
                        public String get() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println(Thread.currentThread().getName() + "运行了...");
                            return finalI +" ok...";
                        }
                    }, pool);
        }
        //可以使用返回值CompletableFuture<String> future来获取任务完成的情况
        //等待数组中所有的任务都执行完。 join会阻塞
        CompletableFuture.allOf(result).join();

        //结果就在数组中，可以获取结果
        List<String> list = Arrays.stream(result)
                .<String>map(new Function<CompletableFuture, String>() {
                    @Override
                    public String apply(CompletableFuture completableFuture) {
                        try {
                            return (String) completableFuture.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .collect(Collectors.toList());

        System.out.println(list);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    /*
        使用stream，可以并行处理集合.

        并行流要注意线程安全问题。
            保证当前的任务，只使用自己的内部的局部变量，不要访问公共的属性，静态属性。

            多线程： 开了N个线程
            并行流:  1个线程，用多核多线程加速，来运行。
     */
    @Test
    public  void testParrallelStream(){
        //计时器
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        IntStream
                .iterate(0,x -> x+ 1)
                .limit(100)
                .parallel() //把一个普通的Stream变成一个可以并行处理的Stream。利用CPU多核多线程技术来处理
                .forEach(x -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(Thread.currentThread().getName() + x);
                });

        stopWatch.stop();
        System.out.println("耗时:"+stopWatch.getLastTaskTimeMillis());


    }
}


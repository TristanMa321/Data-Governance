package com.atguigu.demo.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 任务调度
 *
 * @date 2024-01-26
 */
@Component
public class MyTask{
    @Scheduled(cron = "*/1 * * * * *")
    public void sayHi(){
        System.out.println("hi");
    }
}

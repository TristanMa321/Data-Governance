package com.atguigu.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.LocalDate;

/**
 * @date 2024-01-26
 */

@Configuration  // 容器配置类，容器扫描后会自动加载里面的配置
public class MyConfig {
    @Bean   // 把返回值放入容器，单例 对象只创建一次
    @Scope("prototype")     // 不会提前创建，获取时才会创建，可以创建多次
    public LocalDate getDate(){
        System.out.println("今天的日期被创建了");
        return LocalDate.now();
    }
}

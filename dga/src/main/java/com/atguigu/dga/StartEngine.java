package com.atguigu.dga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling
@SpringBootApplication
@EnableScheduling
public class StartEngine {
    public static void main(String[] args) {
        SpringApplication.run(StartEngine.class,args);
    }
}

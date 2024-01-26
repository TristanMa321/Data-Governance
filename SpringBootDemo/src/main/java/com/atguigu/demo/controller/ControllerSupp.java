package com.atguigu.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date 2024-01-26
 */
/*
// @RequestMapping 可以标记在方法上 或控制器上
// 如果标注在控制器上，会为这个控制器的所有方法，添加一层父路径
    如果要限制url的请求方式：
        可以使用@GetMapping，强制要求url必须发get请求。否则就报错405.
        可以使用@PostMapping，强制要求url必须发post请求。否则就报错405.

 */
@RequestMapping("/a")
@RestController
public class ControllerSupp {
    @RequestMapping("/getparam1")
    public Object handle1(String name, Integer age){
        System.out.println("getparam 控制台输出");
        return "ok";
    }
    @GetMapping("/getparam2")
    public Object handle2(String name, Integer age){
        System.out.println("getparam 控制台输出");
        return "ok";
    }
    @PostMapping("/getparam3")
    public Object handle3(String name, Integer age){
        System.out.println("getparam 控制台输出");
        return "ok";
    }
}

package com.atguigu.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// MVC架构的Controller 不懂就去看markdown吧
// 这个类必须在主启动类所在包或子包下
@Controller
public class HelloController {
    @RequestMapping("/hello")   // 请求映射
    public String hello(){
        System.out.println("控制台输出");
        return "success.html";
    }
}

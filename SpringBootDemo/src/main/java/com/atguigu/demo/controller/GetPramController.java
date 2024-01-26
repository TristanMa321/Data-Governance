package com.atguigu.demo.controller;

import com.atguigu.demo.bean.People;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @date 2024-01-26
 * 请求参数
 *      字面量参数
 *          方式一：平铺直叙
 *          方式二：Bean
 *          方式三：Map 前加@RequestParam
 *      json格式参数
 *          只能通过特殊工具能模拟json格式的参数
 *          bean或map接收 且前加注解@RequestBody
 */
@Controller
public class GetPramController {
    /*
        请求：http://localhost:8080/getparam?name=tom&age=20
     */
    // 字面量参数 方式一 平铺直叙
    @RequestMapping("/getparam")
    public Object handle1(String name, Integer age){
        System.out.println("getparam 控制台输出");
        System.out.println(name+" " +age);
        return "success.html";
    }
    // 方式二：Bean
    @RequestMapping("/getparam2")
    public Object handle2(People people){
        System.out.println("getparam 控制台输出");
        System.out.println(people);
        return "success.html";
    }
    // 方式三：Map
    @RequestMapping("/getparam3")
    public Object handle3(@RequestParam Map<String,Object> map){
        System.out.println("getparam 控制台输出");
        System.out.println(map);
        return "success.html";
    }

    // json格式map  只能接受post请求
    @RequestMapping("/getparam4")
    public Object handle4(@RequestBody Map<String,Object> map){
        System.out.println("getparam 控制台输出");
        System.out.println(map);
        return "success.html";
    }
    // json格式bean
    @RequestMapping("/getparam5")
    public Object handle5(@RequestBody People people) {
        System.out.println("getparam 控制台输出");
        System.out.println(people);
        return "success.html";
    }
    /*
        获取url路径作为参数  http://localhost:8080:/a/b/c
        获取  b c
     */
    @ResponseBody
    @RequestMapping("/a/{haha}/{xxx}")  //{}中写任意字符占位
    public Object handle6(@PathVariable("haha") String f, @PathVariable("xxx")String g){
        System.out.println("getparam 控制台输出");
        System.out.println(f+" "+g);
        return "ok";
    }
}

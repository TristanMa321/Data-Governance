package com.atguigu.demo.controller;

import com.atguigu.demo.bean.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 响应式客户端
 * @date 2024-01-26
 */
/* 分两种 返回页面
         返回数据(方法上加@ResponseBody):  返回字面量 非字面量（框架会自动把非字面量转成json）
*/
//@Controller
@RestController // = @Controller + @ResponseBody
public class ResponseController {
//    @ResponseBody
    @RequestMapping("/response1")
    public Object handle10(@RequestParam Map<String, Object> map){
        return "success.html";
    }

//    @ResponseBody
    @RequestMapping("/response2")
    public Object handle11(@RequestParam Map<String, Object> map){
        Employee employee = new Employee(1, "tom", "a", "b");

        return employee;
    }
}

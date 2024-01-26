package com.atguigu.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component // 自动创建实例  对象name：默认首字母小写
/*
    容器概念：是个map,内部为{dog = Dog@xxxx}
 */
public class Dog
{
    private String name = "旺财";
    private Integer age = 5;

    public Dog(){
        System.err.println(name +"被创建了");
    }
}



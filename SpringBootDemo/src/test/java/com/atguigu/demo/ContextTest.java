package com.atguigu.demo;

import com.atguigu.demo.bean.Dog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;

/**
 * 容器测试
 *
 * @date 2024-01-26
 */

@SpringBootTest     // 加了这个注解，才能使用容器功能
public class ContextTest {
    @Autowired // 自动去容器中，根据变量的类型找对应的对象，找到就赋值
    private Dog dog;
    @Test
    public void test1(){
        System.out.println(dog);
    }

    // 容器（Context）对象
    @Autowired
    private ApplicationContext context;

    @Test
    public void test2(){
        Dog tugou = context.getBean("tugou", Dog.class);
        Dog yanggou = context.getBean("yanggou", Dog.class);
        System.out.println(tugou);
    }

    @Test
    public void test3(){
        // 获取一个LocalDate对象
        System.out.println(context.getBean(LocalDate.class));
    }

}

package com.atguigu.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求参数接受
 *
 * @date 2024-01-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class People {
    private String name;
    private Integer age;
}

package com.atguigu.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试多数据源而出创建的类
 *
 * @date 2024-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    private Integer id;
    private String region_name;
}

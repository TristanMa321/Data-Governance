package com.atguigu.mybatis.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2024-01-23
 */
@Data
@AllArgsConstructor // 全参构造器，但会覆盖掉无参构造器
@NoArgsConstructor  // 避免被覆盖掉
public class Employee {
    private Integer id;
    private String last_name;
    private String gender;
    private String email;
}

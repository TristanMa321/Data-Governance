package com.atguigu.mybatis.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emp
{
    private Integer id;
    private String name;
    private GenderAndEmail genderAndEmail;
}


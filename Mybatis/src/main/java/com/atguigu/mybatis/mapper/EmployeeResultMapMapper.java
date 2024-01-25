package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;

import java.util.List;

/**
 * 接口正常要有实现类
 * 但使用Mybatis, 会通过动态代理技术，自动为接口提供实例
 */
public interface EmployeeResultMapMapper {

    // 查询所有员工
    List<Employee> getAll();

}

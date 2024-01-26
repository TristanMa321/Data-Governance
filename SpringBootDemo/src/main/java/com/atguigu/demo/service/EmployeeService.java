package com.atguigu.demo.service;

import com.atguigu.demo.bean.Employee;

import java.util.List;

public interface EmployeeService {
    // 根据id查询员工
    Employee getEmpById(Integer id);

    // 根据id删除
    void deleteEmpById(Integer id);

    // 更新
    void updateEmp(Employee employee);

    // 插入
    void insertEmp(Employee employee);

    // 查询所有员工
    List<Employee> getAll();



}

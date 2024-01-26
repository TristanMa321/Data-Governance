package com.atguigu.demo.service.implemennt;

import com.atguigu.demo.bean.Employee;
import com.atguigu.demo.mapper.EmployeeMapper;
import com.atguigu.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * EmployeeServie实现类
 *
 * @date 2024-01-26
 */
/*
    @Component:Spring 容器在扫描到这个类时会自动创建该类的实例，并将其作为一个 Bean 管理起来。
 */
@Service  // 特殊的component 专门用于业务逻辑层
public class EmployServiceImp implements EmployeeService {

    @Autowired
    private EmployeeMapper mapper;
    @Override
    public Employee getEmpById(Integer id) {
        System.out.println("查询之前");
        Employee e = mapper.getEmpById(id);
        System.out.println("查询之后");
        return e;
    }

    @Override
    public void deleteEmpById(Integer id) {
        System.out.println("删除之前");
        mapper.deleteEmpById(id);
        System.out.println("删除之后");
    }

    @Override
    public void updateEmp(Employee employee) {
        System.out.println("更新之前");
        mapper.updateEmp(employee);
        System.out.println("更新之后");
    }

    @Override
    public void insertEmp(Employee employee) {
        System.out.println("插入之前");
        mapper.insertEmp(employee);
        System.out.println("插入之后");
    }

    @Override
    public List<Employee> getAll() {
        System.out.println("查询所有数据之前");
        List<Employee> all = mapper.getAll();
        System.out.println("查询所有数据之后");
        return all;
    }
}

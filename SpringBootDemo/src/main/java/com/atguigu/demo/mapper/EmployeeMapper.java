package com.atguigu.demo.mapper;



import com.atguigu.demo.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
    @Mapper
        作用： 容器扫描到后，会容器中创建一个单例对象
              给开发人员看的，标识当前类是一个Dao
              标注了这个注解，容器会调用Mybatis提供的动态代理技术为你创建对象。
 */

@Mapper
public interface EmployeeMapper {
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

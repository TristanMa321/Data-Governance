package com.atguigu.demo.mapper;



import com.atguigu.demo.bean.Employee;
import com.atguigu.demo.bean.Region;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
    @Mapper
        作用： 容器扫描到后，会容器中创建一个单例对象，在Service的implement中真正的调用这个方法
              给开发人员看的，标识当前类是一个Dao，是个数据库访问对象,记住它是跟数据库交互那个
              写sql逻辑的地方，只不过这个工程里，我们没有在这里直接写，而是映射到了一个sql文件
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

    // 查询Mybatis2库下的Region表

    @DS("Mybatis2")
    List<Region> getRegion();

}

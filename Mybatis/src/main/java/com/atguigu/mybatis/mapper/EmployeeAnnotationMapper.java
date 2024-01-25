package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 业务稳定，sql不需要经常修改，可以将sql放在接口中
 * 用什么sql 就写什么注解
 * 别忘了在mybatis全局配置文件中加入
 */
public interface EmployeeAnnotationMapper {
    // 根据id查询员工
    @Select("select * from employee where id = #{feafa}")
    Employee getEmpById(Integer id);

    // 根据id删除
    @Delete("delete from employee where id = #{feafa}")
    void deleteEmpById(Integer id);

    // 更新
    @Update("update employee\n" +
            "            set last_name=#{last_name}, gender=#{gender}, email=#{email}\n" +
            "        where id = #{xxxx}")
    void updateEmp(Employee employee);

    // 插入
    @Insert("insert into employee(last_name, gender, email)\n" +
            "        values (#{last_name},#{gender},#{email})")
    void insertEmp(Employee employee);

    // 查询所有员工
    @Select("select * from employee")
    List<Employee> getAll();

}

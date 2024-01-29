package com.atguigu.mybatisplus;

import com.atguigu.mybatisplus.bean.Employee;
import com.atguigu.mybatisplus.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @date 2024-01-27
 */
@SpringBootTest
public class MybatisPlusTest {

    @Autowired
    private EmployeeService service;
    /*
        QueryWrapper: 删除和查询时使用
        UpdateWrapper: 更新时使用
     */
    @Test
    public void queryTest(){
        // 查询id > 2的gender为male的员工的id和gender字段，且按照id降序排序，取前3
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .select("id", "gender")
                .gt("id", 3)
                .eq("gender", "male")
                .orderByDesc("id")
                .last("limit 3");
        List<Employee> emps = service.list(queryWrapper);
        System.out.println(emps);
    }
    @Test
    public void updateTest(){
        //id > 2的员工的gender更新为male
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<Employee>()
                .gt("id", 2)
                .set("gender", "male");
        service.update(updateWrapper);
    }

    @Test
    public void deleteTest(){
        // 删除id>3的员工
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>()
                .gt("id", 5);
        service.remove( queryWrapper);
    }
}

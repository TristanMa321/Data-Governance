package com.atguigu.mybatisplus.mapper;

import com.atguigu.mybatisplus.bean.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2024-01-28
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}

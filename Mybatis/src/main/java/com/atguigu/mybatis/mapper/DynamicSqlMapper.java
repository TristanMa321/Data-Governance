package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @date 2024-01-29
 */
public interface DynamicSqlMapper {
    //演示动态sql
    List<Employee> queryByCondition(@Param("name") String name,
                                    @Param("id") Integer id,
                                    @Param("gender") String gender);

}

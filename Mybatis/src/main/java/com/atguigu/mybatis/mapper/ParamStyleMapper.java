package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ParamStyleMapper {
    /*
        占位符格式  #{xxx}:自动给参数加‘’
                  ${xxx}:保持参数不变
     */
    @Select("select * from employee where last_name = ${a}")
    Employee getEmpById(@Param("a") String name);
}

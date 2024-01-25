package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import com.atguigu.mybatis.beans.Myparam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;
import java.util.Objects;

public interface PassParamentMapper {
    /* 根据id和last_name查询某个员工
        方式一： 直接写
                将每个参数使用@Param("xxx")手动指定xxx
     */
    @Select("select * from employee where id = #{a} and last_name = #{b}")
    Employee getEmpById(@Param("a") Integer id, @Param("b") String name);
    /*
        方式二： 用bean
                xxx写bean的属性名
     */
    @Select("select * from employee where id = #{a} and last_name = #{b}")
    Employee getEmpById2(Myparam param);
    /*
        方式3： 用Map封装参数
                xxx写key
     */
    @Select("select * from employee where id = #{a} and last_name = #{b}")
    Employee getEmpById3(Map<String, Object> param);
}

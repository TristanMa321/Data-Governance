package com.atguigu.mybatis.demo;

import com.atguigu.mybatis.beans.Employee;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @date 2024-01-23
 */
public class HelloWorld {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis_config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        /*
        1️⃣建立连接
            原生JDBC:Connection
            Mybatis: SqlSession

         */
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        /**
         * Retrieve a list of mapped objects from the statement key and parameter.
         * @param <E> the returned list element type  方法泛型
         * @param statement Unique identifier matching the statement to use.
         * @param parameter 给sql文件占位符传的参数 #{}
         * @return List of mapped object
         */
        // 存在问题 就是这个parameter的类型是object, 我们无法指定类型
        // 所以我们可以自己写一个selectOne
        Object o = sqlSession.<Employee>selectOne("feichangbang.sql1", 1);

        System.out.println(o);
    }
}

package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ParamStyleMapperTest {

    private SqlSessionFactory sqlSessionFactory;

    // 因为是线程不安全的 所以不能再属性处创建一个session 一起用，要每次调用创建一次
    // private SqlSession sqlSession;

    {
        String resource = "mybatis_config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // sqlSession = sqlSessionFactory.openSession();
    }
    @Test
    public void getEmpById() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            ParamStyleMapper mapper = sqlSession.getMapper(ParamStyleMapper.class);
            Employee e= mapper.getEmpById("Tom");
            System.out.println(e);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
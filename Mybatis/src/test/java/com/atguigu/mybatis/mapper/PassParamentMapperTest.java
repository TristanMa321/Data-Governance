package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import com.atguigu.mybatis.beans.Myparam;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PassParamentMapperTest {

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
            PassParamentMapper mapper = sqlSession.getMapper(PassParamentMapper.class);
            Employee e= mapper.getEmpById(1, "Tom");
            System.out.println(e);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void getEmpById2() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PassParamentMapper mapper = sqlSession.getMapper(PassParamentMapper.class);
            Myparam tom = new Myparam(1, "Tom");
            Employee e= mapper.getEmpById2(tom);
            System.out.println(e);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void getEmpById3() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PassParamentMapper mapper = sqlSession.getMapper(PassParamentMapper.class);
            Myparam tom = new Myparam(1, "Tom");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("a",1);
            hashMap.put("b","Tom");
            Employee e= mapper.getEmpById3(hashMap);
            System.out.println(e);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
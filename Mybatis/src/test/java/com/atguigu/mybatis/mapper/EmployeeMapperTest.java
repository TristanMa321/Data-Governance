package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.beans.Employee;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class EmployeeMapperTest {

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
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee e= mapper.getEmpById(1);
            System.out.println(e);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 写操作 必须提交事务
    @Test
    public void deleteEmpById() {
        // 参数true 自动提交
        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            mapper.deleteEmpById(5);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void getAll() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            List<Employee> all = mapper.getAll();
            System.out.println(all);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void insertEmp() {
        try(SqlSession sqlSession = sqlSessionFactory.openSession()) {
            EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = new Employee(1, "Tristan", "male", "2420");
            mapper.insertEmp(employee);
            // 手动提交事务
            sqlSession.commit();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
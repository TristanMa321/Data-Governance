package com.atguigu.mybatis.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class DynamicSqlMapperTest {

    private SqlSessionFactory sqlSessionFactory;

    {
        String resource = "mybatis_config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }
    @Test
    public void queryByCondition() {
         SqlSession sqlSession = null;
           try {
                sqlSession = sqlSessionFactory.openSession();
               DynamicSqlMapper mapper = sqlSession.getMapper(DynamicSqlMapper.class);
               System.out.println(mapper.queryByCondition(
                       null,
                       null,
                       "male"
               ));

           } finally {
               sqlSession.close();
          }
    }
}

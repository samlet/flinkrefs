package com.bluecc.fixtures;

import com.bluecc.fixtures.mapper.PersonMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.inject.Singleton;
import java.io.InputStream;

@Singleton
public class MyBatisFac {
    SqlSessionFactory sqlSessionFactory;
    MyBatisFac(){
        InputStream inputStream = MyBatisProcs.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSessionFactory.getConfiguration().addMapper(PersonMapper.class);
    }

    public SqlSession openSession(){
        return sqlSessionFactory.openSession();
    }
}

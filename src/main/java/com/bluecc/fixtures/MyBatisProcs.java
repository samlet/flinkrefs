package com.bluecc.fixtures;

import com.bluecc.fixtures.mapper.Person;
import com.bluecc.fixtures.mapper.PersonMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisProcs {
    public static void main(String[] args) throws IOException {
        MyBatisFac fac=Modules.build().getInstance(MyBatisFac.class);
        try (SqlSession session = fac.openSession()) {

            queryPerson(session);
        }
    }

    private static void queryPerson(SqlSession session) {
        PersonMapper mapper = session.getMapper(PersonMapper.class);
        Person blog = mapper.selectPerson("system");

        System.out.println(blog);
    }
}

/*
⊕ [MYBATIS - Annotations](https://www.tutorialspoint.com/mybatis/mybatis_annotations.htm)

 */
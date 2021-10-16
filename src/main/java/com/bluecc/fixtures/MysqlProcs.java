package com.bluecc.fixtures;

import com.bluecc.fixtures.mapper.Student;
import com.bluecc.fixtures.mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 CREATE TABLE test.student(
     ID int(10) NOT NULL AUTO_INCREMENT,
     NAME varchar(100) NOT NULL,
     BRANCH varchar(255) NOT NULL,
     PERCENTAGE int(3) NOT NULL,
     PHONE int(11) NOT NULL,
     EMAIL varchar(255) NOT NULL,
     PRIMARY KEY (`ID`)
 );

 */
public class MysqlProcs extends SqlProcs{
    MysqlProcs(SqlSession session) {
        super(session);
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = MysqlProcs.class.getClassLoader().getResourceAsStream("mybatis-config-mysql.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSessionFactory.getConfiguration().addMapper(StudentMapper.class);

        // 在所有代码中都遵循这种使用模式，可以保证所有数据库资源都能被正确地关闭。
        try (SqlSession session = sqlSessionFactory.openSession()) {
            MysqlProcs procs=new MysqlProcs(session);
            int id=procs.insertProc();
            procs.updateProc( id);

            procs.queryProc( id);
            procs.listProc();

            procs.deleteProc( id);
        }
    }

}

/*
⊕ [MYBATIS - Annotations](https://www.tutorialspoint.com/mybatis/mybatis_annotations.htm)

 */
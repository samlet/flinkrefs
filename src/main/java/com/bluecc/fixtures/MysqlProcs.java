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
public class MysqlProcs {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = MysqlProcs.class.getClassLoader().getResourceAsStream("mybatis-config-mysql.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSessionFactory.getConfiguration().addMapper(StudentMapper.class);

        // 在所有代码中都遵循这种使用模式，可以保证所有数据库资源都能被正确地关闭。
        try (SqlSession session = sqlSessionFactory.openSession()) {
            int id=insertProc(session);
            updateProc(session, id);

            queryProc(session, id);
            listProc(session);

            deleteProc(session, id);
        }
    }

    private static void deleteProc(SqlSession session, int id) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        mapper.delete(id);
        System.out.println("record deleted successfully");
        session.commit();
    }

    private static void listProc(SqlSession session) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        List<Student> rs=mapper.getAll();
        rs.forEach(System.out::println);
    }

    private static void queryProc(SqlSession session, int id) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        //Get the student details
        Student student = mapper.getById(id);
        System.out.println(student.getBranch());
        System.out.println(student.getEmail());
        System.out.println(student.getId());
        System.out.println(student.getName());
        System.out.println(student.getPercentage());
        System.out.println(student.getPhone());
    }

    private static void updateProc(SqlSession session, int id) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        //select a particular student using id
        Student student = mapper.getById(id);

        if(student!=null) {
            System.out.println("Current details of the student are " + student.toString());

            //Set new values to the mail and phone number of the student
            student.setEmail("Shyam123@yahoo.com");
            student.setPhone(984802233);

            //Update the student record
            mapper.update(student);
            System.out.println("Record updated successfully");
            session.commit();
        }
    }

    private static Integer insertProc(SqlSession session) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);

        //Create a new student object
        Student student = new Student();

        //Set the values
        student.setName("zara");
        student.setBranch("EEE");
        student.setEmail("zara@gmail.com");
        student.setPercentage(90);
        student.setPhone(123412341);

        //Insert student data
        mapper.insert(student);

        System.out.println("record inserted successfully: "+student.getId());
        session.commit();
        return student.getId();
    }
}

/*
⊕ [MYBATIS - Annotations](https://www.tutorialspoint.com/mybatis/mybatis_annotations.htm)

 */
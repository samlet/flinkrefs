package com.bluecc.fixtures;

import com.bluecc.fixtures.mapper.Student;
import com.bluecc.fixtures.mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class SqlProcs {
    protected SqlSession session;

    public SqlProcs(SqlSession session) {
        this.session = session;
    }

    protected void deleteProc(int id) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        mapper.delete(id);
        System.out.println("record deleted successfully");
        session.commit();
    }

    protected void listProc() {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        List<Student> rs = mapper.getAll();
        rs.forEach(System.out::println);
    }

    protected void queryProc(int id) {
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

    protected void updateProc(int id) {
        StudentMapper mapper = session.getMapper(StudentMapper.class);
        //select a particular student using id
        Student student = mapper.getById(id);

        if (student != null) {
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

    protected Integer insertProc() {
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

        System.out.println("record inserted successfully: " + student.getId());
        session.commit();
        return student.getId();
    }
}

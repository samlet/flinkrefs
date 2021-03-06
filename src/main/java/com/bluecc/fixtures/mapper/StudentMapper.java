package com.bluecc.fixtures.mapper;
import java.util.List;

import org.apache.ibatis.annotations.*;
public interface StudentMapper {

    final String getAll = "SELECT * FROM student";
    final String getById = "SELECT * FROM student WHERE ID = #{id}";
    final String deleteById = "DELETE from student WHERE ID = #{id}";
    final String insert = "INSERT INTO student (NAME, BRANCH, PERCENTAGE, PHONE, EMAIL ) VALUES (#{name}, #{branch}, #{percentage}, #{phone}, #{email})";
    final String update = "UPDATE student SET EMAIL = #{email}, NAME = #{name}, BRANCH = #{branch}, PERCENTAGE = #{percentage}, PHONE = #{phone} WHERE ID = #{id}";

    @Select(getAll)
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "branch", column = "BRANCH"),
            @Result(property = "percentage", column = "PERCENTAGE"),
            @Result(property = "phone", column = "PHONE"),
            @Result(property = "email", column = "EMAIL")
    })

    List<Student> getAll();

    @Select(getById)
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "name", column = "NAME"),
            @Result(property = "branch", column = "BRANCH"),
            @Result(property = "percentage", column = "PERCENTAGE"),
            @Result(property = "phone", column = "PHONE"),
            @Result(property = "email", column = "EMAIL")
    })

    Student getById(int id);

    @Update(update)
    void update(Student student);

    @Delete(deleteById)
    void delete(int id);

    @Insert(insert)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Student student);
}

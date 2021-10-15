package com.bluecc.fixtures.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public interface PersonMapper {
    @Select("SELECT * FROM person WHERE party_id = #{id}")
    @Results(value = {
            @Result(property = "partyId", column = "party_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name")
    })
    Person selectPerson(@Param("id")String id);
}

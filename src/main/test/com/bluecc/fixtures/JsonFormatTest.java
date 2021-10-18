package com.bluecc.fixtures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.junit.Test;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class JsonFormatTest {
    @Test
    public void testGson(){
        Gson gson=new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Email obj=new Email(2,2,"tianjin");
        String jsonStr=gson.toJson(obj);
        System.out.println(jsonStr);
        Email newObj=gson.fromJson(jsonStr, Email.class);
        System.out.println(newObj);
    }

    @Test
    public void testGsonWithData(){
        Gson gson=new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                .create();
        EmailData obj=new EmailData();
        obj.setId(1);
        obj.setEmployeeId(2);
        obj.setAddress("beijing");

        String jsonStr=gson.toJson(obj);
        System.out.println(jsonStr);
        Email newObj=gson.fromJson(jsonStr, Email.class);
        System.out.println(newObj);
    }

    @Data
    public static class EmailData {
        private Integer id;
        private Integer employeeId;
        private String address;
    }
}

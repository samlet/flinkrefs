package com.bluecc.refs.fastjson;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

public class JsonObjectTest {
    @Test
    public void testFastJson(){
        JSONObject jo=new JSONObject();
        jo.put("name", "samlet");
        jo.put("age", "33");
        System.out.println(jo.toString());
    }
}

/*
1. 将对象序列化成json字符串
String com.alibaba.fastjson.JSON.toJSONString(Object object)

2. 将json字符串反序列化成对象
<T> Project com.alibaba.fastjson.JSON.parseObject(String text, Class<T> clazz)

3. 将json字符串反序列化成JSON对象
JSONObject com.alibaba.fastjson.JSON.parseObject(String text)

4.根据key 得到json中的json数组
JSONArray com.alibaba.fastjson.JSONObject.getJSONArray(String key)

5. 根据下标拿到json数组的json对象
JSONObject com.alibaba.fastjson.JSONArray.getJSONObject(int index)

6.. 根据key拿到json的字符串值
String com.alibaba.fastjson.JSONObject.getString(String key)

7. 根据key拿到json的int值
int com.alibaba.fastjson.JSONObject.getIntValue(String key)

8. 根据key拿到json的boolean值
boolean com.alibaba.fastjson.JSONObject.getBooleanValue(String key)
 */


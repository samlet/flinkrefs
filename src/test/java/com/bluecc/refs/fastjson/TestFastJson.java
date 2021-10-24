package com.bluecc.refs.fastjson;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TestFastJson {

    public static void main(String args[]) {
        TestFastJson tfj = new TestFastJson();
        Project prj = tfj.init();
        String json = tfj.getJsonString(prj);
        System.out.println("json=" + json);
        //json={"l_factory":[{"fcName":"东软","l_worker":[{"age":30,"name":"乔佳飞","sex":"男"},{"age":25,"name":"李帅飞","sex":"女"}]},{"fcName":"亚信","l_worker":[{"age":26,"name":"王新峰","sex":"男"},{"age":0}]}],"pjName":"接口自动化","waibao":true}
        System.out.println("waibao=" + tfj.getJsonValueObj(json, "waibao", Boolean.class));
        //waibao=true
        JSONArray array = (JSONArray) tfj.getJsonValueObj(json, "l_factory", JSONArray.class);
        System.out.println("array=" + array.toString());
        //array=[{"fcName":"东软","l_worker":[{"sex":"男","name":"乔佳飞","age":30},{"sex":"女","name":"李帅飞","age":25}]},{"fcName":"亚信","l_worker":[{"sex":"男","name":"王新峰","age":26},{"age":0}]}]
        String jsonArr = tfj.getJsonArrayValue(array, 0, "fcName");
        System.out.println("fcName=" + jsonArr);
        //fcName=东软
        JSONArray array2 = tfj.getJsonArrayValueIsArray(array, 0, "l_worker");
        System.out.println("array2=" + array2.toString());
        //array2=[{"sex":"男","name":"乔佳飞","age":30},{"sex":"女","name":"李帅飞","age":25}]
        String json2 = tfj.getJsonArrayValue(array2, 0);
        System.out.println("json2=" + json2);
        //json2={"sex":"男","name":"乔佳飞","age":30}

        /*以下输出
        name=乔佳飞
        sex=男
        age=30
        jsonArr2=男

         * */
        System.out.println("name=" + tfj.getJsonValueObj(json2, "name", String.class));
        System.out.println("sex=" + tfj.getJsonValueObj(json2, "sex", String.class));
        System.out.println("age=" + tfj.getJsonValueObj(json2, "age", Integer.class));

        String jsonArr2 = tfj.getJsonArrayValue(array2, 0, "sex");
        System.out.println("jsonArr2=" + jsonArr2);

        /*以下输出
         接口自动化
        东软
        乔佳飞
         */
        System.out.println(tfj.getJsonValue(json));
        System.out.println(tfj.getJsonValue(json, "l_factory"));
        System.out.println(tfj.getJsonValue(json, "l_factory", "l_worker"));

    }

    public static void main1(String args[]) {
        TestFastJson tfj = new TestFastJson();
        Project prj = tfj.init();
        String json = tfj.getJsonString(prj);
        prj.setPjName("序列化后修改pjname");
        System.out.println(prj.getPjName());//序列化后修改pjname
        Project po = JSON.parseObject(json, Project.class);
        System.out.println(po.getPjName());//接口自动化
    }

    public void tt(Class clazz) {
        System.out.println(clazz.getSimpleName());
        if (clazz.getName().equals("String")) {
            System.out.println("stringllala");
        }
    }

    public Project init() {
        Project pj = new Project();
        Factory ft1 = new Factory();
        Factory ft2 = new Factory();
        Worker wk1 = new Worker();
        wk1.setName("乔佳飞");
        wk1.setSex("男");
        wk1.setAge(30);

        Worker wk2 = new Worker();
        wk2.setName("李帅飞");
        wk2.setSex("女");
        wk2.setAge(25);

        Worker wk3 = new Worker();
        wk3.setName("魏晓博");
        wk3.setSex("男");
        wk3.setAge(27);

        Worker wk4 = new Worker();
        wk3.setName("王新峰");
        wk3.setSex("男");
        wk3.setAge(26);

        List<Worker> workers1 = new ArrayList<Worker>();
        workers1.add(wk1);
        workers1.add(wk2);

        List<Worker> workers2 = new ArrayList<Worker>();
        workers2.add(wk3);
        workers2.add(wk4);

        ft1.setFcName("东软");
        ft1.setL_worker(workers1);

        ft2.setFcName("亚信");
        ft2.setL_worker(workers2);

        List<Factory> factorys = new ArrayList<Factory>();
        factorys.add(ft1);
        factorys.add(ft2);

        pj.setPjName("接口自动化");
        pj.setWaibao(true);
        pj.setL_factory(factorys);

        return pj;
    }

    /**
     * 将对象转换成json
     */
    public String getJsonString(Object obj) {
        String json = JSON.toJSONString(obj);
        return json;
    }

    /**
     * 根据key得到json的value
     */
    public String getJsonValue(String json) {
        JSONObject jo = JSON.parseObject(json);
        String value = jo.getString("pjName");
        return value;
    }

    /**
     * 根据key得到json的集合
     */
    public JSONArray getJsonArray(String json, String key) {
        JSONObject jo = JSON.parseObject(json);

        JSONArray array = jo.getJSONArray(key);

        return array;
    }

    /**
     * 根据下标得到json数组的值
     */
    public String getJsonArrayValue(JSONArray array, int index) {
        JSONObject jo_fc = array.getJSONObject(index);
        String json = jo_fc.toJSONString();
        return json;
    }

    /**
     * 根据下标得到json数组的值，再根据key得到该值的value，该值类型是String
     */
    public String getJsonArrayValue(JSONArray array, int index, String key) {
        JSONObject jo_fc = array.getJSONObject(index);
        String value = jo_fc.getString(key);
        return value;
    }

    /**
     * 根据下标得到json数组的值，再根据key得到该值的value,该值类型是JSONArray
     */
    public JSONArray getJsonArrayValueIsArray(JSONArray array, int index, String key) {
        JSONObject jo_fc = array.getJSONObject(index);
        JSONArray arrayNew = jo_fc.getJSONArray(key);
        return arrayNew;
    }

    /**
     * 根据对象的类型，自动识别获取该对象的值
     */
    public Object getJsonValueObj(String json, String key, Class clazz) {
        JSONObject jo = JSON.parseObject(json);
        if (clazz.getSimpleName().equals("String")) {
            String value = jo.getString(key);
            return value;
        } else if (clazz.getSimpleName().equals("Integer")) {
            Integer value = jo.getInteger(key);
            return value;
        } else if (clazz.getSimpleName().equals("Boolean")) {
            Boolean value = jo.getBoolean(key);
            return value;
        } else if (clazz.getSimpleName().equals("JSONArray")) {
            JSONArray array = jo.getJSONArray(key);
            return array;
        } else {
            return "error, 暂不支持的类型:" + clazz.toString();
        }

    }

    public String getJsonValue(String json, String key) {
        JSONObject jo = JSON.parseObject(json);

        JSONArray array = jo.getJSONArray(key);
        JSONObject jo_fc = array.getJSONObject(0);
        String value = jo_fc.getString("fcName");
        return value;
    }

    public String getJsonValue(String json, String key, String keyW) {
        JSONObject jo = JSON.parseObject(json);

        JSONArray array = jo.getJSONArray(key);
        JSONObject jo_fc = array.getJSONObject(0);
        JSONArray arrayW = jo_fc.getJSONArray(keyW);
        JSONObject jo_wk = arrayW.getJSONObject(0);
        String value = jo_wk.getString("name");
        int age = jo_wk.getIntValue("age");
        //System.out.println(age);
        return value;
    }
}
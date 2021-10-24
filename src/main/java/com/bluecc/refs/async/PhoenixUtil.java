package com.bluecc.refs.async;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [UNUSED] move to sqlflow-project
 */
public class PhoenixUtil {
    private static Connection conn = null;

    public static void init(){
        try {
            Class.forName("org.apache.phoenix.queryserver.client.Driver");
            conn = DriverManager.getConnection("jdbc:phoenix:thin:url=http://localhost:8765/PHOENIX;serialization=PROTOBUF", "lucky", "pratama");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find driver");
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // 从Phoenix中查询数据
    // select * from 表 where XXX=xxx
    public static <T> List<T> queryTypedList(String sql,Class<T> clazz){
        if(conn == null){
            init();
        }
        List<T> resultList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取数据库操作对象
            ps = conn.prepareStatement(sql);
            //执行SQL语句
            rs = ps.executeQuery();
            //通过结果集对象获取元数据信息
            ResultSetMetaData metaData = rs.getMetaData();
            //处理结果集
            while (rs.next()){
                //声明一个对象，用于封装查询的一条结果集
                T rowData = clazz.newInstance();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    BeanUtils.setProperty(rowData,metaData.getColumnName(i),rs.getObject(i));
                }
                resultList.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("从维度表中查询数据失败");
        } finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultList;
    }

    public static List<JSONObject> queryList(String sql){
        if(conn == null){
            init();
        }
        List<JSONObject> resultList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取数据库操作对象
            ps = conn.prepareStatement(sql);
            //执行SQL语句
            rs = ps.executeQuery();
            //通过结果集对象获取元数据信息
            ResultSetMetaData metaData = rs.getMetaData();
            //处理结果集
            while (rs.next()){
                //声明一个对象，用于封装查询的一条结果集
                JSONObject rowData = new JSONObject();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    rowData.put(metaData.getColumnName(i),rs.getObject(i));
                }
                resultList.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("从维度表中查询数据失败");
        } finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultList;
    }

    public static void main(String[] args) {
        System.out.println(queryList("select * from users"));
    }

}

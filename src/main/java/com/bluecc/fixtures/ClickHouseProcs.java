package com.bluecc.fixtures;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $ just clickhouse-cli
     CREATE TABLE sales
     (
     `Region` text NULL,
     `Country` text NULL,
     `Item Type` text NULL,
     `Sales Channel` text NULL,
     `Order Priority` text NULL,
     `Order Date` DateTime NULL,
     `Order ID` Int8 NULL,
     `Ship Date` DateTime NULL,
     `Units Sold` Int8 NULL,
     `Unit Price` FLOAT NULL,
     `Unit Cost` FLOAT NULL,
     `Total Revenue` FLOAT NULL,
     `Total Cost` FLOAT NULL,
     `Total Profit` FLOAT NULL
     )
     ENGINE = Log
 */
public class ClickHouseProcs {
    public static void main(String[] args) {
        String sqlDB = "show databases";//查询数据库
        String sqlTab = "show tables";//查看表
        String sqlCount = "select count(*) count from sales";//查询sales数据量
        exeSql(sqlDB);
        exeSql(sqlTab);
        exeSql(sqlCount);
    }

    @SuppressWarnings("rawtypes")
    public static void exeSql(String sql) {
        String address = "jdbc:clickhouse://localhost:8123/default";
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try {
            Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
            connection = DriverManager.getConnection(address);
            statement = connection.createStatement();
            long begin = System.currentTimeMillis();
            results = statement.executeQuery(sql);
            long end = System.currentTimeMillis();
            System.out.println("执行（" + sql + "）耗时：" + (end - begin) + "ms");
            ResultSetMetaData rsmd = results.getMetaData();
            List<Map> list = new ArrayList<>();
            while (results.next()) {
                Map<String,String> map = new HashMap<>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    map.put(rsmd.getColumnName(i), results.getString(rsmd.getColumnName(i)));
                }
                list.add(map);
            }
            for (Map map : list) {
                System.err.println(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//关闭连接
            try {
                if (results != null) {
                    results.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.bluecc.fixtures;

import com.bluecc.fixtures.mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $ just clickhouse-cli
 * or POST(plain) http://localhost:8123/
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

 * $ just ft ClickHouseProcs

 * Others:
     CREATE TABLE hits (
     id Int32,
     created_day Date,
     type String,
     user_id Int32,
     location_id Int32,
     created_at Int32
     ) ENGINE = MergeTree PARTITION BY toMonday(created_day)
     ORDER BY (created_at, id) SETTINGS index_granularity = 8192;

     CREATE TABLE student
     (
     ID Int32 NOT NULL,
     NAME text NULL,
     BRANCH text NULL,
     PERCENTAGE Int8 NULL,
     PHONE Int32 NULL,
     EMAIL text NULL
     )
     ENGINE = Log

     CREATE TABLE test(a String, b UInt8, c FixedString(1)) ENGINE = Log
     INSERT INTO test (a,b,c) values
         ('user_1',1,'1')
         ('user_2',2,'5')
         ('user_3',3,'5')
         ('user_1',1,'5')
         ('user_4',4,'5')
         ('user_5',5,'5')
     SELECT count(*) from test
 */
public class ClickHouseProcs extends SqlProcs{
    public ClickHouseProcs(SqlSession session) {
        super(session);
    }

    public static void main(String[] args) {
//        useRawSql();

        InputStream inputStream = MysqlProcs.class.getClassLoader()
                .getResourceAsStream("mybatis-config-clickhouse.xml");
//                .getResourceAsStream("mybatis-config-clickhouse-native.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSessionFactory.getConfiguration().addMapper(StudentMapper.class);

        // 在所有代码中都遵循这种使用模式，可以保证所有数据库资源都能被正确地关闭。
        try (SqlSession session = sqlSessionFactory.openSession()) {

            ClickHouseProcs procs=new ClickHouseProcs(session);
//            int id=procs.insertProc();  // java.sql.SQLFeatureNotSupportedException
//            procs.updateProc( id);
//            procs.queryProc( id);
            procs.listProc();
//            procs.deleteProc( id);
        }
    }

    static void useRawSql(){
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

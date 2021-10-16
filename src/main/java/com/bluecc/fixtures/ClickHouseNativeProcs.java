package com.bluecc.fixtures;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class ClickHouseNativeProcs {
    public static void main(String[] args) throws SQLException {
        initDataSource();

        pureTest();
//        dropAndCreate();
        insertAndSelect();
    }

    static HikariDataSource ds;
    static void initDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:clickhouse://127.0.0.1:9000");
        config.setDriverClassName("com.github.housepower.jdbc.ClickHouseDriver");
        config.setUsername( "default" );
        config.setPassword( "" );
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
    }

    private static void insertAndSelect() throws SQLException {
        try (Connection connection = getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("drop table if exists test_jdbc_example")) {
                    System.out.println(rs.next());
                }
                try (ResultSet rs = stmt.executeQuery("create table test_jdbc_example(day Date, name String, age UInt8) Engine=Log")) {
                    System.out.println(rs.next());
                }
                try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO test_jdbc_example VALUES(?, ?, ?)")) {
                    for (int i = 1; i <= 200; i++) {
                        pstmt.setDate(1, new Date(System.currentTimeMillis()));
                        if (i % 2 == 0)
                            pstmt.setString(2, "Zhang San" + i);
                        else
                            pstmt.setString(2, "Zhang San");
                        pstmt.setByte(3, (byte) ((i % 4) * 15));
                        System.out.println(pstmt);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }

                try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from test_jdbc_example where age>? and age<=?")) {
                    pstmt.setByte(1, (byte) 10);
                    pstmt.setByte(2, (byte) 30);
                    printCount(pstmt);
                }

                try (PreparedStatement pstmt = connection.prepareStatement("select count(*) from test_jdbc_example where name=?")) {
                    pstmt.setString(1, "Zhang San");
                    printCount(pstmt);
                }
                try (ResultSet rs = stmt.executeQuery("drop table test_jdbc_example")) {
                    System.out.println("drop");
                }
            }
        }
    }

    private static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection("jdbc:clickhouse://127.0.0.1:9000");
        return ds.getConnection();
    }

    private static void printCount(PreparedStatement pstmt) throws SQLException {
        System.out.println(pstmt);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
//                System.out.println(rs.getString(1) + "\t" + rs.getInt(3));
                System.out.println("count: "+rs.getInt(1));
            }
        }
    }

    static void dropAndCreate() throws SQLException {
        try (Connection connection = getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("drop table if exists test_jdbc_example");
                stmt.executeQuery("create table test_jdbc_example(" +
                        "day default toDate( toDateTime(timestamp) ), " +
                        "timestamp UInt32, " +
                        "name String, " +
                        "impressions UInt32" +
                        ") Engine=MergeTree(day, (timestamp, name), 8192)");
                stmt.executeQuery("alter table test_jdbc_example add column costs Float32");
                stmt.executeQuery("drop table test_jdbc_example");
            }
        }
    }

    private static void pureTest() throws SQLException {
        try (Connection connection = getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(
                        "SELECT (number % 3 + 1) as n, sum(number) FROM numbers(10000000) GROUP BY n")) {
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "\t" + rs.getLong(2));
                    }
                }
            }
        }
    }
}

/*
 * ref:
 *  https://housepower.github.io/ClickHouse-Native-JDBC/guide/jdbc_driver.html#jdbc-driver
 *  https://housepower.github.io/ClickHouse-Native-JDBC/guide/connection_pool.html#connection-pool
 */

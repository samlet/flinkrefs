package com.bluecc.fixtures;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MysqlFacTest {

//    @Test
//    public void getConnection() throws SQLException {
//        MysqlFac fac = Modules.build().getInstance(MysqlFac.class);
//    }

    @Test
    public void testMapList() {
        MysqlFac fac = Modules.build().getInstance(MysqlFac.class);
        MapListHandler beanListHandler = new MapListHandler();

        try (Connection connection = fac.getConnection()) {
            QueryRunner runner = new QueryRunner();
            List<Map<String, Object>> list
                    = runner.query(connection, "SELECT * FROM users", beanListHandler);
            System.out.println(list.get(0).get("name"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testMapById() {
        MysqlFac fac = Modules.build().getInstance(MysqlFac.class);
        MapListHandler beanListHandler = new MapListHandler();

        try (Connection connection = fac.getConnection()) {
            QueryRunner runner = new QueryRunner();
            List<Map<String, Object>> list
                    = runner.query(connection,
                    "SELECT * FROM users where id=?",
                    beanListHandler, 1);
            System.out.println("result: "+list.size());
            System.out.println(list.get(0).get("name"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
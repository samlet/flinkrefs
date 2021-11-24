package com.bluecc.fixtures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.math.BigInteger;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbiProcs {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        int id;
        String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGroup {
        int id;
        List<Integer> userIds;
    }

    public static void main(String[] args) throws SQLException {
        // Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test"); // (H2 in-memory database)
        ClickHouseFac clickHouseFac = new ClickHouseFac();
        // Jdbi jdbi = Jdbi.create(clickHouseFac.getConnection());
        Jdbi jdbi = Jdbi.create(clickHouseFac.getDataSource());
        init(jdbi);

        processUsers(jdbi);
        processGroups(jdbi);
        processArrays(jdbi);
    }

    private static void processUsers(Jdbi jdbi) {
        List<User> users = jdbi.withHandle(handle -> {
            // handle.execute("CREATE TABLE user (id INTEGER PRIMARY KEY, name VARCHAR)");
            // create table user(id UInt64, name String, age UInt8) Engine=Log
            handle.execute("create table if not exists user(id UInt64, name String, age UInt8) Engine=Log");

            // Inline positional parameters
            handle.execute("INSERT INTO user(id, name) VALUES (?, ?)", 0, "Alice");

            // Positional parameters
            handle.createUpdate("INSERT INTO user(id, name) VALUES (?, ?)")
                    .bind(0, 1) // 0-based parameter indexes
                    .bind(1, "Bob")
                    .execute();

            // Named parameters
            handle.createUpdate("INSERT INTO user(id, name) VALUES (:id, :name)")
                    .bind("id", 2)
                    .bind("name", "Clarice")
                    .execute();

            // Named parameters from bean properties
            handle.createUpdate("INSERT INTO user(id, name) VALUES (:id, :name)")
                    .bindBean(new User(3, "David"))
                    .execute();

            // Easy mapping to any type
            return handle.createQuery("SELECT * FROM user ORDER BY name")
                    .mapToBean(User.class)
                    .list();
        });

        users.forEach(e -> System.out.println(e));
    }

    private static void processGroups(Jdbi jdbi) {
        // jdbi.registerColumnMapper(new UserIdColumnMapper());
        UserGroup rec = jdbi.withHandle(handle -> {
            // create table groups (id UInt64, user_ids Array(UInt64))
            handle.execute("create table if not exists groups (id UInt64, user_ids Array(UInt64)) Engine=Log");

            handle.createUpdate("insert into groups (id, user_ids) values (:id, :userIds)")
                    .bind("id", 1)
                    // .bind("userIds", "[10, 5, 70]")
                    .bind("userIds", new BigInteger[] { BigInteger.valueOf(10),
                            BigInteger.valueOf(5), BigInteger.valueOf(70) })
                    .execute();

            UserGroup group = handle.createQuery("select id, user_ids from groups where id = :id")
                    .bind("id", 1)
                    // .mapTo(new GenericType<List<Integer>>() {})
                    .map(new UserGroupMapper())
                    // .one();
                    .first();  // 因为clickhouse允许重复id, 所以不能用one, 因为one约束只允许返回一个值
            return group;
        });
        System.out.println(rec);

        List<UserGroup> ids = jdbi.withHandle(handle -> {
            // create table groups (id UInt64, user_ids Array(UInt64))
            handle.execute("create table if not exists groups (id UInt64, user_ids Array(UInt64)) Engine=Log");

            handle.createUpdate("insert into groups (id, user_ids) values (:id, :userIds)")
                    .bind("id", 1)
                    // .bind("userIds", "[10, 5, 70]")
                    // .bind("userIds", new int[] { 10, 5, 70 })
                    .bind("userIds", new BigInteger[] { BigInteger.valueOf(10),
                            BigInteger.valueOf(5), BigInteger.valueOf(70) })
                    .execute();

            List<UserGroup> groups = handle.createQuery("select id, user_ids from groups")
                    .map(new UserGroupMapper())
                    .list();
            return groups;
        });
        // System.out.println(ids);
        ids.forEach(e -> System.out.println(e));
    }

    static class UserGroupMapper implements RowMapper<UserGroup> {
        @Override
        public UserGroup map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new UserGroup(rs.getInt("id"),
                    getArray(rs, "user_ids"));
        }

        List<Integer> getArray(ResultSet rs, String col) throws SQLException {
            List<Integer> result= Lists.newArrayList();
            Array sqlArray=rs.getArray(col);
            for(Object o:(Object[])sqlArray.getArray()){
                result.add(Integer.valueOf(o.toString()));
            }
            return result;
        }
    }


    public static class UserIdColumnMapper implements ColumnMapper<List<Integer>> {
        @Override
        public List<Integer> map(ResultSet rs, int col, StatementContext ctx) throws SQLException {
            List<Integer> result= Lists.newArrayList();
            Array sqlArray=rs.getArray(col);
            for(Object o:(Object[])sqlArray.getArray()){
                result.add(Integer.valueOf(o.toString()));
            }
            return result;
        }
    }

    // public static class ClickHouseArrayType implements SqlArrayType<com.github.housepower.jdbc.ClickHouseArray> {
    //     @Override
    //     public String getTypeName() {
    //         return "integer";
    //     }
    //
    //     @Override
    //     public Object convertArrayElement(ClickHouseArray element) {
    //         return element.toString();
    //     }
    // }

    private static void init(Jdbi db) {
        db.registerArrayType(Integer.class, "Int32");
        db.registerArrayType(BigInteger.class, "UInt64");
        db.registerArrayType(String.class, "String");
    }
    private static void processArrays(Jdbi jdbi) {

        jdbi.withHandle(handle -> {
            handle.execute("create table if not exists player_stats ("
                    + "name String, "
                    + "seasons Array(String), "
                    + "points Array(Int32)) Engine=Log");
            handle.createUpdate("insert into player_stats (name,seasons,points) values (?,?,?)")
                    .bind(0, "Jack Johnson")
                    .bind(1, new String[]{"2013-2014", "2014-2015", "2015-2016"})
                    .bind(2, new Integer[]{42, 51, 50})
                    .execute();

            // java.sql.SQLFeatureNotSupportedException
            // ClickHouseArray未实现com.github.housepower.jdbc.wrapper.SQLArray.getResultSet
            // String[] seasons = handle.createQuery("select seasons from player_stats where name=:name")
            //         .bind("name", "Jack Johnson")
            //         .mapTo(String[].class)
            //         .one();
            // assertThat(seasons).containsExactly("2013-2014", "2014-2015", "2015-2016");

            List<String> seasons = handle.createQuery("select seasons from player_stats where name=:name")
                    .bind("name", "Jack Johnson")
                    .map((rs, ctx) -> {
                        Array sqlArray=rs.getArray("seasons");
                        List<String> result=Lists.newArrayList();
                        for(Object o:(Object[])sqlArray.getArray()){
                            result.add(o.toString());
                        }
                        return result;
                    }).first();
            System.out.println(seasons);
            return null;
        });
    }
}
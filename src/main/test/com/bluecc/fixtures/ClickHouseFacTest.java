package com.bluecc.fixtures;

import com.bluecc.util.StringUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

import static org.junit.Assert.*;

/*
POST http://localhost:8123/

create table emp(
  empno    Int32,
  ename    text,
  job      text,
  mgr      Int32,
  hiredate date,
  sal      Int32,
  comm     Int32,
  deptno   Int32
)
 ENGINE = Log

insert into emp values(
 7839, 'KING', 'PRESIDENT', null,
 '2019-01-01',
 7698, null, 10
)
(
 7698, 'BLAKE', 'MANAGER', 7839,
 '2019-01-01',
 7782, null, 20
);
 */
public class ClickHouseFacTest {

    @Test
    public void getConnection() throws SQLException {
        ClickHouseFac fac = Modules.build().getInstance(ClickHouseFac.class);
        List<Employee> rs = fetchData(fac);
        rs.forEach(System.out::println);
    }

    public static List<Employee> fetchData(ClickHouseFac fac) {
        final String SQL_QUERY = "select * from emp";
        List<Employee> employees = null;
        try (Connection con = fac.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery();) {
            employees = new ArrayList<Employee>();
            Employee employee;
            while (rs.next()) {
                employee = new Employee();
                employee.setEmpNo(rs.getInt("empno"));
                employee.setEname(rs.getString("ename"));
                employee.setJob(rs.getString("job"));
                employee.setMgr(rs.getInt("mgr"));
                employee.setHiredate(rs.getDate("hiredate"));
                employee.setSal(rs.getInt("sal"));
                employee.setComm(rs.getInt("comm"));
                employee.setDeptno(rs.getInt("deptno"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Test
    public void testMapList() {
        ClickHouseFac fac = Modules.build().getInstance(ClickHouseFac.class);
        MapListHandler beanListHandler = new MapListHandler();

        try (Connection connection = fac.getConnection()) {
            QueryRunner runner = new QueryRunner();
            List<Map<String, Object>> list
                    = runner.query(connection, "SELECT * FROM emp", beanListHandler);
            System.out.println(list.get(0).get("ename"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testObjectList() {
        ClickHouseFac fac = Modules.build().getInstance(ClickHouseFac.class);
        BeanListHandler<Employee> beanListHandler
                = new BeanListHandler<>(Employee.class);

        try (Connection connection = fac.getConnection()) {
            QueryRunner runner = new QueryRunner();
            List<Employee> employeeList
                    = runner.query(connection, "SELECT * FROM emp", beanListHandler);

            employeeList.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testInsert() {
        ClickHouseFac fac = Modules.build().getInstance(ClickHouseFac.class);
        QueryRunner runner = new QueryRunner();
        try (Connection connection = fac.getConnection()) {
            String insertSQL
                    = "INSERT INTO email (id, employee_id, address) "
                    + "VALUES (?, ?, ?)";

            Object[] values=new Object[]{1, 1, "beijing"};
            int numRowsInserted = runner.update(connection, insertSQL, values);

            assertEquals(numRowsInserted, 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void testInsertBuilder() {
        ClickHouseFac fac = Modules.build().getInstance(ClickHouseFac.class);
        QueryRunner runner = new QueryRunner();

        try (Connection connection = fac.getConnection()) {
            Email obj=new Email(2,2,"tianjin");
            // Load all fields in the class (private included)
            Field [] attributes =  obj.getClass().getDeclaredFields();

            List<String> flds= Lists.newArrayList();
            List<Object> values=Lists.newArrayList();
            for (Field field : attributes) {
                // Dynamically read Attribute Name
                System.out.println("ATTRIBUTE NAME: " + field.getName());
                Object val=PropertyUtils.getSimpleProperty(obj, field.getName());
                System.out.println("ATTRIBUTE VALUE: " + val);
                flds.add(StringUtil.camelToSnake(field.getName()));
                values.add(val);
            }
            String nameList=String.join(", ", flds);
            String placerList= String.join(", ", Collections.nCopies(flds.size(), "?"));
            String sql=String.format("INSERT INTO email (%s) "
                    + "VALUES (%s)", nameList, placerList);
            System.out.println(sql);

            int numRowsInserted = runner.update(connection, sql, values.toArray());
            assertEquals(numRowsInserted, 1);
        } catch (SQLException | InvocationTargetException | IllegalAccessException | NoSuchMethodException throwables) {
            throwables.printStackTrace();
        }
    }
}


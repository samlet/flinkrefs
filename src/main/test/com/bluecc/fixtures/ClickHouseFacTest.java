package com.bluecc.fixtures;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
        ClickHouseFac fac=Modules.build().getInstance(ClickHouseFac.class);
        List<Employee> rs=fetchData(fac);
        rs.forEach(System.out::println);
    }

    public static List<Employee> fetchData(ClickHouseFac fac) {
        final String SQL_QUERY = "select * from emp";
        List<Employee> employees = null;
        try (Connection con = fac.getConnection(); PreparedStatement pst = con.prepareStatement(SQL_QUERY); ResultSet rs = pst.executeQuery();) {
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
}


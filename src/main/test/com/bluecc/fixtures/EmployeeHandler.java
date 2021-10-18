package com.bluecc.fixtures;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.*;
import java.util.List;

public class EmployeeHandler extends BeanListHandler<Employee> {

    private Connection connection;

    public EmployeeHandler(Connection con) {
        super(Employee.class);
        this.connection = con;
    }

    @Override
    public List<Employee> handle(ResultSet rs) throws SQLException {
        List<Employee> employees = super.handle(rs);

        QueryRunner runner = new QueryRunner();
        BeanListHandler<Email> handler = new BeanListHandler<>(Email.class);
        String query = "SELECT * FROM email WHERE employeeid = ?";

        for (Employee employee : employees) {
            List<Email> emails
                    = runner.query(connection, query, handler, employee.getEmpNo());
//            employee.setEmails(emails);
        }
        return employees;
    }
}
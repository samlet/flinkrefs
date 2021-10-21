package com.bluecc.fixtures.metadata;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);

    private Connection connection;

    public DatabaseConfig() {
        try {
            Class.forName("org.h2.Driver");
            String url = "jdbc:h2:mem:testdb";
            connection = DriverManager.getConnection(url, "sa", "");
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void init() {
        createTables();
        createViews();
    }

    private void createTables() {
        try {
            connection.createStatement().executeUpdate("create table CUSTOMER (ID int primary key auto_increment, NAME VARCHAR(45))");
            connection.createStatement().executeUpdate("create table CUST_ADDRESS (ID VARCHAR(36), CUST_ID int, ADDRESS VARCHAR(45), FOREIGN KEY (CUST_ID) REFERENCES CUSTOMER(ID))");
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

    private void createViews() {
        try {
            connection.createStatement().executeUpdate("CREATE VIEW CUSTOMER_VIEW AS SELECT * FROM CUSTOMER");
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }
}
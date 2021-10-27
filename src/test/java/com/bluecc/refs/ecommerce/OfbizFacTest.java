package com.bluecc.refs.ecommerce;

import com.bluecc.fixtures.ClickHouseFac;
import com.bluecc.fixtures.Employee;
import com.bluecc.fixtures.Modules;
import com.bluecc.refs.ecommerce.beans.Person;
import com.bluecc.refs.ecommerce.beans.Product;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OfbizFacTest {

    @Test
    void getConnection() {
        OfbizFac fac = Modules.build().getInstance(OfbizFac.class);
        BeanListHandler<Person> beanListHandler
                = new BeanListHandler<>(Person.class,
                new BasicRowProcessor(new GenerousBeanProcessor()));

        try (Connection connection = fac.getConnection()) {
            QueryRunner runner = new QueryRunner();
            List<Person> personList
                    = runner.query(connection, "SELECT * FROM person", beanListHandler);

            personList.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void getProducts() {
        OfbizFac fac = Modules.build().getInstance(OfbizFac.class);
        BeanListHandler<Product> beanListHandler
                = new BeanListHandler<>(Product.class,
                new BasicRowProcessor(new GenerousBeanProcessor()));

        try (Connection connection = fac.getConnection()) {
            QueryRunner runner = new QueryRunner();
            List<Product> personList
                    = runner.query(connection, "SELECT * FROM product", beanListHandler);

            personList.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
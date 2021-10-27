package com.bluecc.refs.ecommerce;

import com.bluecc.refs.async.ThreadPoolUtil;
import com.bluecc.refs.ecommerce.beans.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class OfbizDim {
    @Slf4j
    static class ProductDim extends RichAsyncFunction<String, Product> {
        private static final long serialVersionUID = 1;
        private ExecutorService executorService;
        private OfbizFac fac;

        public ProductDim() {
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            executorService = ThreadPoolUtil.getInstance();
            this.fac = new OfbizFac();
        }

        @Override
        public void asyncInvoke(String key, ResultFuture<Product> resultFuture) throws Exception {
            executorService.submit(
                    () -> {
                        BeanListHandler<Product> beanListHandler
                                = new BeanListHandler<>(Product.class,
                                new BasicRowProcessor(new GenerousBeanProcessor()));

                        try (Connection connection = fac.getConnection()) {
                            QueryRunner runner = new QueryRunner();
                            List<Product> personList
                                    = runner.query(connection,
                                    "SELECT * FROM product where product_id = ?",
                                    beanListHandler, key);

                            resultFuture.complete(personList);
                        } catch (SQLException throwables) {
                            log.error(throwables.getLocalizedMessage(), throwables);
                            resultFuture.complete(new ArrayList<>(0));
                        }
                    });
        }
    }
}


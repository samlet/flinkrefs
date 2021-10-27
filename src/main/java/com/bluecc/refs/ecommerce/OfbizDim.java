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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.lang.String.format;

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

    @Slf4j
    static class EntityDim<T> extends RichAsyncFunction<String, T> {
        private static final long serialVersionUID = 1;
        private final String table;
        private ExecutorService executorService;
        private OfbizFac fac;
        private final Class<T> clz;

        public EntityDim(Class<T> clz, String table) {
            this.clz = clz;
            this.table = table;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            executorService = ThreadPoolUtil.getInstance();
            this.fac = new OfbizFac();
        }

        @Override
        public void asyncInvoke(String key, ResultFuture<T> resultFuture) throws Exception {
            executorService.submit(
                    () -> {
                        BeanListHandler<T> beanListHandler
                                = new BeanListHandler<>(clz,
                                new BasicRowProcessor(new GenerousBeanProcessor()));

                        try (Connection connection = fac.getConnection()) {
                            QueryRunner runner = new QueryRunner();
                            List<T> entityList
                                    = runner.query(connection,
                                    format("SELECT * FROM %s where %s_id = ?", table, table),
                                    beanListHandler, key);

                            resultFuture.complete(entityList);
                        } catch (SQLException throwables) {
                            log.error(throwables.getLocalizedMessage(), throwables);
                            resultFuture.complete(new ArrayList<>(0));
                        }
                    });
        }
    }

    /**
     * Typed entity-dim injector
     *
     * @param <T> input and output object type
     * @param <D> data list element type which query from db
     */
    @Slf4j
    public static abstract class TypedEntityDim<T, D> extends RichAsyncFunction<T, T> implements IDimJoin<T,D>{
        private static final long serialVersionUID = 1;
        private final String table;
        String primKey;
        private ExecutorService executorService;
        private OfbizFac fac;
        private final Class<D> clz;

        public TypedEntityDim(Class<D> clz, String table, String primKey) {
            this.clz = clz;
            this.table = table;
            this.primKey=primKey;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            executorService = ThreadPoolUtil.getInstance();
            this.fac = new OfbizFac();
        }

        @Override
        public void asyncInvoke(T input, ResultFuture<T> resultFuture) throws Exception {
            executorService.submit(
                    () -> {
                        BeanListHandler<D> beanListHandler
                                = new BeanListHandler<>(clz,
                                new BasicRowProcessor(new GenerousBeanProcessor()));

                        try (Connection connection = fac.getConnection()) {
                            QueryRunner runner = new QueryRunner();
                            List<D> entityList
                                    = runner.query(connection,
                                    format("SELECT * FROM %s where %s = ?", table, primKey),
                                    beanListHandler, getKey(input));
                            if(!entityList.isEmpty()) {
                                join(input, entityList);
                            }
                            resultFuture.complete(Collections.singletonList(input));
                        } catch (SQLException throwables) {
                            log.error(throwables.getLocalizedMessage(), throwables);
                            resultFuture.complete(new ArrayList<>(0));
                        }
                    });
        }

    }
}


package com.bluecc.refs.sqlflow;

import com.bluecc.fixtures.Modules;
import com.google.inject.Injector;
import lombok.Data;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * $ just run sqlflow.JdbcPipeline
 */
public class JdbcPipeline {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        Injector injector=Modules.build();
        PrefabManager prefabManager=injector.getInstance(PrefabManager.class);

        prefabManager.define(tEnv, "source_mysql", "addresses");
        prefabManager.define(tEnv, "sink_kafka", "addresses_output");

        String sql = "select * from addresses";
        Table ResultTable = tEnv.sqlQuery(sql);

        DataStream<Tuple2<Boolean, Row>> resultDS = tEnv.toRetractStream(ResultTable, Row.class);
        resultDS.print();

        DataStream<Address> addressesDS = tEnv.toAppendStream(ResultTable, Address.class);
        addressesDS.print(">>>");

        tEnv.executeSql("insert into addresses_output select * from " + ResultTable);
        env.execute();
    }

    @Data
    public static class Address{
        int id;
        int user_id;
        String email_address;
    }
}

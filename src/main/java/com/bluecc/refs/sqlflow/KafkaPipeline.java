package com.bluecc.refs.sqlflow;

import com.bluecc.fixtures.Modules;
import com.google.inject.Injector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * $ just run generator.UserGeneratorJob -sleep 500   # 0.5s
 * $ just run sqlflow.KafkaPipeline
 * $ just consume output_kafka
 */
public class KafkaPipeline {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        Injector injector=Modules.build();
        PrefabManager prefabManager=injector.getInstance(PrefabManager.class);

        prefabManager.execute(tEnv, "source_kafka", "user_info_input");
        prefabManager.execute(tEnv, "source_kafka", "user_info_output");

        String sql = "select * from user_info_input " +
                "where user_level = '1'";

        Table ResultTable = tEnv.sqlQuery(sql);

        DataStream<Tuple2<Boolean, Row>> resultDS = tEnv.toRetractStream(ResultTable, Row.class);
        resultDS.print();

        //将满足条件的记录存储到 output_kafka 落地表中
        tEnv.executeSql("insert into user_info_output select * from "+ResultTable);

        env.execute();
    }
}

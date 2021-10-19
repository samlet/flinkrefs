package com.bluecc.refs.sqlflow;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * - input_kafka 的 topic ，基于这个topic 创建一个临时表 input_kafka
 * - 基于output_kafka 的topic ， output_kafka 表
 * - 读出来每一条数据并过滤出来 status="success" 数据
 * - insert into output_kafka select * from input_kafka
 * - 直接在 output_kafka 这个topic 消费到数据
 */
public class KafkaProcs {
    public static void main(String[] args) throws Exception {
        //1.准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //创建流表环境
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        //2.Source
        //从 kafka 中直接映射到输入表
        TableResult inputTable = tEnv.executeSql(
                "CREATE TABLE input_kafka (\n" +
                        "  `user_id` BIGINT,\n" +
                        "  `page_id` BIGINT,\n" +
                        "  `status` STRING\n" +
                        ") WITH (\n" +
                        "  'connector' = 'kafka',\n" +  //连接的数据源是 kafka
                        "  'topic' = 'input_kafka',\n" + //映射的主题topic
                        "  'properties.bootstrap.servers' = 'localhost:9092',\n" + //kafka地址
                        "  'properties.group.id' = 'default',\n" + //kafka消费的消费组
                        "  'scan.startup.mode' = 'latest-offset',\n" + //从最新的位置扫描
                        "  'format' = 'csv'\n" +  //扫描的数据是格式：csv格式
                        ")"
        );
        //从 kafka 中映射一个输出表
        TableResult outputTable = tEnv.executeSql(
                "CREATE TABLE output_kafka (\n" +
                        "  `user_id` BIGINT,\n" +
                        "  `page_id` BIGINT,\n" +
                        "  `status` STRING\n" +
                        ") WITH (\n" +
                        "  'connector' = 'kafka',\n" +
                        "  'topic' = 'output_kafka',\n" +
                        "  'properties.bootstrap.servers' = 'localhost:9092',\n" +
                        "  'format' = 'json',\n" +
                        "  'sink.partitioner' = 'round-robin'\n" +  //分区的方式，轮训
                        ")"
        );

        String sql = "select " +
                "user_id," +
                "page_id," +
                "status " +
                "from input_kafka " +
                "where status = 'success'";

        Table ResultTable = tEnv.sqlQuery(sql);

        DataStream<Tuple2<Boolean, Row>> resultDS = tEnv.toRetractStream(ResultTable, Row.class);
        resultDS.print();

        //将满足 status = 'success' 的记录存储到 output_kafka 落地表中
        tEnv.executeSql("insert into output_kafka select * from "+ResultTable);

        //7.excute
        env.execute();
    }
}

/*
$ just produce input_kafka
    kafka-console-producer --broker-list localhost:9092 --topic input_kafka
    >1,1,pv
    >1,2,success

    4> (true,+I[1, 2, success])

$ just list output_kafka
    kafka-console-consumer --bootstrap-server localhost:9092 --topic output_kafka --from-beginning
    {"user_id":1,"page_id":2,"status":"success"}
 */

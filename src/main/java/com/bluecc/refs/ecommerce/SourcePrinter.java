package com.bluecc.refs.ecommerce;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * $ just run ecommerce.SourcePrinter -topic ods_base_db_m
 */
public class SourcePrinter {
    public static void main(String[] args) throws Exception {
        ParameterTool parameters = ParameterTool.fromArgs(args);
        String topic = parameters.get("topic", "ods_base_db_m");
        String groupId = parameters.get("group", "base_db_app_group");
        boolean earliest=parameters.getBoolean("earliest", true);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        FlinkKafkaConsumer<String> kafkaSource = KafkaHelper.getKafkaSource(topic, groupId, earliest);
        DataStreamSource<String> jsonStrDS = env.addSource(kafkaSource);
        SingleOutputStreamOperator<JSONObject> jsonObjDS = jsonStrDS
                .map(jsonStr -> JSON.parseObject(jsonStr));

        // 对数据进行ETL   如果table为空 或者 data为空，或者长度<3  ，将这样的数据过滤掉
        SingleOutputStreamOperator<JSONObject> filteredDS = jsonObjDS.filter(
                jsonObj -> {
                    return jsonObj.getString("table") != null
                            && jsonObj.getJSONObject("data") != null
                            && jsonObj.getString("data").length() > 3;
                }
        );

        // .....
        filteredDS.print();

        env.execute();
    }

}

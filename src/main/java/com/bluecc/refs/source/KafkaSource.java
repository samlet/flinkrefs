package com.bluecc.refs.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * $ just run source.KafkaSource -topic data    # 等待数据
 * $ python -m mysql_tool dump hotel bot *sensor   # 从数据库中导出数据
 * $ just produce sensor    # 或, 从命令行输入数据
 */
public class KafkaSource {
    public static void main(String[] args) throws Exception{
        ParameterTool parameters = ParameterTool.fromArgs(args);
        String topic = parameters.get("topic", "sensor");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties properties = getKafkaProperties();

        DataStream<String> dataStream = env.addSource(
                new FlinkKafkaConsumer<String>(topic, new SimpleStringSchema(), properties));

        // 打印输出
        dataStream.print();

        env.execute();
    }

    public static Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "consumer-group");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        return properties;
    }
}

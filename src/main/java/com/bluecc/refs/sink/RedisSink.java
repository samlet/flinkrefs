package com.bluecc.refs.sink;


import com.bluecc.refs.sensor.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

/*
$ just run sink.RedisSink
$ redis-cli

•	因为最新数据覆盖前面的，所以最后redis里呈现的是最新的数据。
localhost:0> hgetall sensor_temp
1) "sensor_1"
2) "37.1"
3) "sensor_6"
4) "15.4"
5) "sensor_7"
6) "6.7"
7) "sensor_10"
8) "38.1"

 */
public class RedisSink {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读取数据
        DataStream<String> inputStream = env.readTextFile("dataset/sensor.txt");

        // 转换成SensorReading类型
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
            String[] fields = line.split(",");
            return new SensorReading(fields[0], Long.valueOf(fields[1]), new Double(fields[2]));
        });

        // 定义jedis连接配置
        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
                .setHost("localhost")
                .setPort(6379)
                .build();

        dataStream.addSink(new org.apache.flink.streaming.connectors.redis.RedisSink<>(config, new MyRedisMapper()));

        env.execute();
    }

    // 自定义RedisMapper
    public static class MyRedisMapper implements RedisMapper<SensorReading> {
        // 定义保存数据到redis的命令，存成Hash表，hset sensor_temp id temperature
        @Override
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "sensor_temp");
        }

        @Override
        public String getKeyFromData(SensorReading data) {
            return data.getId();
        }

        @Override
        public String getValueFromData(SensorReading data) {
            return data.getTemperature().toString();
        }
    }
}

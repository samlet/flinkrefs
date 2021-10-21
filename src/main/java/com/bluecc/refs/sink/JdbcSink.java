package com.bluecc.refs.sink;

import com.bluecc.refs.sensor.SensorReading;
import com.bluecc.refs.source.SourceUDF;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * $ mysql -uroot -proot
    CREATE DATABASE `flink_test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
    CREATE TABLE `sensor_temp` (
      `id` varchar(32) NOT NULL,
      `temp` double NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 * $ just run sink.JdbcSink
    mysql> SELECT * FROM sensor_temp;
 */
public class JdbcSink {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从文件读取数据
//        DataStream<String> inputStream = env.readTextFile("dataset/sensor.txt");
//
//        // 转换成SensorReading类型
//        DataStream<SensorReading> dataStream = inputStream.map(line -> {
//            String[] fields = line.split(",");
//            return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
//        });

        DataStream<SensorReading> dataStream = env.addSource(new SourceUDF.MySensorSource());

        dataStream.addSink(new MyJdbcSink());

        env.execute();
    }

    // 实现自定义的SinkFunction
    public static class MyJdbcSink extends RichSinkFunction<SensorReading> {
        // 声明连接和预编译语句
        Connection connection = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        @Override
        public void open(Configuration parameters) throws Exception {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/flink_test", "root", "root");
            insertStmt = connection.prepareStatement("insert into sensor_temp (id, temp) values (?, ?)");
            updateStmt = connection.prepareStatement("update sensor_temp set temp = ? where id = ?");
        }

        // 每来一条数据，调用连接，执行sql
        @Override
        public void invoke(SensorReading value, Context context) throws Exception {
            // 直接执行更新语句，如果没有更新那么就插入
            updateStmt.setDouble(1, value.getTemperature());
            updateStmt.setString(2, value.getId());
            updateStmt.execute();
            if( updateStmt.getUpdateCount() == 0 ){
                insertStmt.setString(1, value.getId());
                insertStmt.setDouble(2, value.getTemperature());
                insertStmt.execute();
            }
        }

        @Override
        public void close() throws Exception {
            insertStmt.close();
            updateStmt.close();
            connection.close();
        }
    }
}

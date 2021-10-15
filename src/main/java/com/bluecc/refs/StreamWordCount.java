package com.bluecc.refs;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * $ just run StreamWordCount
 * $ nc -lk 7777
 * $ just run StreamWordCount --source socket --host localhost --port 7777
 */
public class StreamWordCount {
    public static void main(String[] args) throws Exception {
        // 创建流处理执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//        env.disableOperatorChaining();

//        System.out.println("Args: " + String.join(", ", args));
        Arrays.stream(args).forEach(System.out::println);

        ParameterTool parameters = ParameterTool.fromArgs(args);
        String sourceFrom = parameters.get("source", "file");
        DataStream<String> inputDataStream;

        if (sourceFrom.equals("file")) {
            System.out.println(".. from file");
            // 从文件中读取数据
            String inputPath = "dataset/hello.txt";
            inputDataStream = env.readTextFile(inputPath);
        } else {
            System.out.println(".. from socket");
            // 用parameter tool工具从程序启动参数中提取配置项
            String host = parameters.get("host");
            int port = parameters.getInt("port");

            // 从socket文本流读取数据
            inputDataStream = env.socketTextStream(host, port);
        }

        // 基于数据流进行转换计算
        DataStream<Tuple2<String, Integer>> resultStream = inputDataStream
                .flatMap(new MyFlatMapper())
//                .slotSharingGroup("green")
                .keyBy(0)
                .sum(1);
//                .setParallelism(2).slotSharingGroup("red");

//        resultStream.print().setParallelism(1);
        resultStream.print();

        // 执行任务
        env.execute();
    }


    // 自定义类，实现FlatMapFunction接口
    public static class MyFlatMapper implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            // 按空格分词
            String[] words = value.split(" ");
            // 遍历所有word，包成二元组输出
            for (String word : words) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }
}

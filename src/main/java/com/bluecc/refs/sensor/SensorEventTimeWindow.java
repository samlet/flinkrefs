package com.bluecc.refs.sensor;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

/** 并行任务Watermark传递测试
 * $ just socket
 sensor_1,1547718199,35.8
 sensor_6,1547718201,15.4
 sensor_7,1547718202,6.7
 sensor_10,1547718205,38.1
 sensor_1,1547718207,36.3
 sensor_1,1547718211,34
 sensor_1,1547718212,31.9
 sensor_1,1547718212,31.9
 sensor_1,1547718212,31.9
 sensor_1,1547718212,31.9

 * $ just run sensor.SensorEventTimeWindow
 •	注意：上面nc端数据全部输入后，才突然有下面4条输出！
 minTemp:2> SensorReading{id='sensor_10', timestamp=1547718205, temperature=38.1}
 minTemp:3> SensorReading{id='sensor_1', timestamp=1547718199, temperature=35.8}
 minTemp:4> SensorReading{id='sensor_7', timestamp=1547718202, temperature=6.7}
 minTemp:3> SensorReading{id='sensor_6', timestamp=1547718201, temperature=15.4}

 •	从TumblingProcessingTimeWindows类里的assignWindows方法，我们可以得知窗口的起点计算方法如下：
 $$ 窗口起点start = timestamp - (timestamp -offset+WindowSize) % WindowSize $$ 由于我们没有设置offset，
 所以这里start=第一个数据的时间戳1547718199-(1547718199-0+15)%15=1547718195
 •	计算得到窗口初始位置为Start = 1547718195，那么这个窗口理论上本应该在1547718195+15的位置关闭，
 也就是End=1547718210

 •	测试代码中Watermark设置的maxOutOfOrderness最大乱序程度是2s，所以实际获取到End+2s的时间戳数据时（达到Watermark），
 才认为Window需要输出计算的结果（不关闭，因为设置了允许迟到1min）
 •	所以实际应该是1547718212的数据到来时才触发Window输出计算结果。

 ? 为什么上面输入中，最后连续四条相同输入，才触发Window输出结果？
 	我们在map才设置Watermark，map根据Rebalance轮询方式分配数据。所以前4个输入分别到4个slot中，
 4个slot计算得出的Watermark不同（分别是1547718199-2，1547718201-2，1547718202-2，1547718205-2）
 ....
 o	根据Watermark的定义，我们认为>=Watermark的数据都已经到达。由于此时watermark >= 窗口End，
 所以Window输出计算结果（4个子任务，4个结果）。
 */
public class SensorEventTimeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(4);

        // Flink1.12.X+ 已经默认就是使用EventTime了，所以不需要这行代码
        // env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(100);

        // socket文本流
        DataStream<String> inputStream = env.socketTextStream("localhost", 7777);

        // 转换成SensorReading类型，分配时间戳和watermark
        DataStream<SensorReading> dataStream = inputStream.map(line -> {
                    String[] fields = line.split(",");
                    return new SensorReading(fields[0], new Long(fields[1]), new Double(fields[2]));
                })

                // 乱序数据设置时间戳和watermark
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(2)) {
                    @Override
                    public long extractTimestamp(SensorReading element) {
                        return element.getTimestamp() * 1000L;
                    }
                });

        OutputTag<SensorReading> outputTag = new OutputTag<SensorReading>("late") {
        };

        // 基于事件时间的开窗聚合，统计15秒内温度的最小值
        SingleOutputStreamOperator<SensorReading> minTempStream = dataStream.keyBy("id")
//                .timeWindow(Time.seconds(15))
                .window(TumblingEventTimeWindows.of(Time.seconds(15)))
                .allowedLateness(Time.minutes(1))
                .sideOutputLateData(outputTag)
                .minBy("temperature");

        minTempStream.print("minTemp");
        minTempStream.getSideOutput(outputTag).print("late");

        env.execute();
    }
}

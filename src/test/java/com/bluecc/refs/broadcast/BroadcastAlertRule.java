package com.bluecc.refs.broadcast;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

/**
 * Desc: 集合变量管广播的情况下 读取该集合的数据后就会 task 就会 finished
 */
@Slf4j
public class BroadcastAlertRule {
    final static MapStateDescriptor<String, String> ALERT_RULE = new MapStateDescriptor<>(
            "alert_rule",
            BasicTypeInfo.STRING_TYPE_INFO,
            BasicTypeInfo.STRING_TYPE_INFO);


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        List<String> strings = Arrays.asList("A", "B", "C");

//        env.socketTextStream("127.0.0.1", 9200)
        env.fromElements("a", "b", "C")
                .connect(env.fromCollection(strings).broadcast(ALERT_RULE))
                .process(new BroadcastProcessFunction<String, String, String>() {
                    @Override
                    public void processElement(String value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
                        log.info("-- process {}", value);
                        ReadOnlyBroadcastState<String, String> broadcastState = ctx.getBroadcastState(ALERT_RULE);
                        if (broadcastState.contains(value)) {
                            out.collect(value);
                        }
                    }

                    @Override
                    public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
                        BroadcastState<String, String> broadcastState = ctx.getBroadcastState(ALERT_RULE);
                        log.info("== put alert element {}", value);
                        broadcastState.put(value, value);
                    }
                })
                .print();

        env.execute();
    }
}


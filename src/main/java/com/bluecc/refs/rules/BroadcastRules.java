package com.bluecc.refs.rules;

import com.bluecc.refs.rules.beans.Person;
import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bluecc.refs.rules.RuleBroadcastProcessFunction.ALERT_RULE;
import static com.bluecc.refs.source.KafkaSource.getKafkaProperties;

/**
 * $ just create rules
 * $ just create person
 * $ just run rules.BroadcastRules -runner config
 * $ python -m feeder send_test_rules
 */
public class BroadcastRules {

    public static void main(String[] args) throws Exception {
        ParameterTool parameters = ParameterTool.fromArgs(args);
        String rulesTopic = parameters.get("rules", "rules");
        String sourceTopic = parameters.get("source", "person");

        String runner = parameters.get("runner", "simple");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        if (runner.equals("simple")) {
            simpleEval(env);
        } else {
            configurableEval(env, rulesTopic, sourceTopic);
        }
    }

    static Gson gson = new Gson();

    private static void configurableEval(StreamExecutionEnvironment env,
                                         String rulesTopic, String sourceTopic) throws Exception {
        env.setParallelism(1);
        DataStream<Person> sourceStream = env.addSource(
                        new FlinkKafkaConsumer<>(sourceTopic,
//                        new JSONKeyValueDeserializationSchema(false),
                                new SimpleStringSchema(),
                                getKafkaProperties()))
                .map(json -> gson.fromJson(json, Person.class));
        DataStream<String> rulesUpdateStream = env.addSource(
                new FlinkKafkaConsumer<>(rulesTopic, new SimpleStringSchema(), getKafkaProperties()));

        BroadcastStream<String> rulesStream = rulesUpdateStream.broadcast(ALERT_RULE);
        sourceStream
                .connect(rulesStream)
                .process(new RuleBroadcastProcessFunction())
                .print();

        env.execute();
    }

    private static void simpleEval(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);

        List<String> ruleCfgs = Arrays.asList("adult-rule.yml", "alcohol-rule.yml");

        env.addSource(
                        new TestSourceFunction()).setParallelism(1)
                .connect(env.fromCollection(ruleCfgs).broadcast(ALERT_RULE))
                .process(new PersonBroadcastProcessFunction())
                .print();

        env.execute();
    }

    private static class TestSourceFunction extends RichSourceFunction<Person> {

        private volatile boolean isRunning = true;

        //测试数据集
        private Person[] dataSet = {
                new Person("Tom", 14),
                new Person("Kitty", 19),
                new Person("Samlet", 84)
        };

        /**
         * 模拟每3秒随机产生1条消息
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void run(SourceContext<Person> ctx) throws Exception {
            int size = dataSet.length;
            while (isRunning) {
                TimeUnit.SECONDS.sleep(3);
                int seed = (int) (Math.random() * size);
                ctx.collect(dataSet[seed]);
                System.out.println("读取到上游发送的消息：" + dataSet[seed]);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }

    }
}

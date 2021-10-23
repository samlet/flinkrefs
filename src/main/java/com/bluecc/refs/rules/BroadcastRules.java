package com.bluecc.refs.rules;

import com.bluecc.refs.rules.beans.Person;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.FileReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BroadcastRules {
    final static MapStateDescriptor<String, String> ALERT_RULE = new MapStateDescriptor<>(
            "alcohol_rule",
            BasicTypeInfo.STRING_TYPE_INFO,
            BasicTypeInfo.STRING_TYPE_INFO);


    static RulesEngine rulesEngine = new DefaultRulesEngine();
    static MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        List<String> strings = Arrays.asList("adult-rule.yml", "alcohol-rule.yml");

        env.addSource(
                new RichSourceFunction<Person>() {

                    private volatile boolean isRunning = true;

                    //测试数据集
                    private Person[] dataSet = {
                            new Person("Tom", 14),
                            new Person("Kitty", 19),
                            new Person("Samlet", 84)
                    };

                    /**
                     * 模拟每3秒随机产生1条消息
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

                }).setParallelism(1)
                .connect(env.fromCollection(strings).broadcast(ALERT_RULE))
                .process(new BroadcastProcessFunction<Person, String, String>() {
                    Rules rules;
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        rules = new Rules();
                    }

                    @Override
                    public void processElement(Person value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
//                        log.info("-- process {}", value);
                        System.out.println("- element: "+value);
//                        ReadOnlyBroadcastState<String, String> broadcastState = ctx.getBroadcastState(ALERT_RULE);
//                        if (broadcastState.contains(value)) {
//                            out.collect(value);
//                        }
                        System.out.println("total rules "+rules.size());
                        Facts facts = new Facts();
                        facts.put("person", value);
                        rulesEngine.fire(rules, facts);
                        System.out.println("person: "+value);
                    }

                    @Override
                    public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
                        BroadcastState<String, String> broadcastState = ctx.getBroadcastState(ALERT_RULE);
//                        log.info("== put alert element {}", value);
                        System.out.println("broadcast: "+value);
                        broadcastState.put(value, value);
                        Rule alcoholRule = ruleFactory.createRule(new FileReader("rules/"+value));
                        rules.register(alcoholRule);
                    }
                })
                .print();

        env.execute();
    }
}

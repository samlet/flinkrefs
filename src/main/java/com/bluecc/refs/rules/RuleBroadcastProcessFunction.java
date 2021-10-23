package com.bluecc.refs.rules;

import com.bluecc.refs.rules.beans.Person;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;

import java.io.FileReader;
import java.io.StringReader;

public class RuleBroadcastProcessFunction extends BroadcastProcessFunction<Person, String, String> {
    Rules rules;

    static RulesEngine rulesEngine = new DefaultRulesEngine();
    static MVELRuleFactory ruleFactory = new MVELRuleFactory(new JsonRuleDefinitionReader());
    final static MapStateDescriptor<String, String> ALERT_RULE = new MapStateDescriptor<>(
            "alcohol_rule",
            BasicTypeInfo.STRING_TYPE_INFO,
            BasicTypeInfo.STRING_TYPE_INFO);

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        rules = new Rules();
    }

    @Override
    public void processElement(Person value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
//                        log.info("-- process {}", value);
        System.out.println("- element: " + value);
//                        ReadOnlyBroadcastState<String, String> broadcastState = ctx.getBroadcastState(ALERT_RULE);
//                        if (broadcastState.contains(value)) {
//                            out.collect(value);
//                        }
        System.out.println("- total rules " + rules.size());
        if (rules.size() > 0) {
            Facts facts = new Facts();
            facts.put("person", value);
            rulesEngine.fire(rules, facts);
            System.out.println("- record: " + value);

            System.out.println();
        }
    }

    @Override
    public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
        BroadcastState<String, String> broadcastState = ctx.getBroadcastState(ALERT_RULE);
//                        log.info("== put alert element {}", value);
        System.out.println("broadcast: " + value);
        broadcastState.put(value, value);
        Rule alcoholRule = ruleFactory.createRule(new StringReader(value));
        rules.register(alcoholRule);
    }
}

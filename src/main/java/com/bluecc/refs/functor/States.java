package com.bluecc.refs.functor;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

public class States {
    private static class Event {
        public final String key;
        public final long timestamp;

        private Event(String key, long timestamp) {
            this.key = key;
            this.timestamp = timestamp;
        }
    }

    public static class Deduplicator extends RichFlatMapFunction<Event, Event> {
        ValueState<Boolean> keyHasBeenSeen;

        @Override
        public void open(Configuration conf) {
            ValueStateDescriptor<Boolean> desc = new ValueStateDescriptor<>("keyHasBeenSeen", Types.BOOLEAN);
            keyHasBeenSeen = getRuntimeContext().getState(desc);
        }

        @Override
        public void flatMap(Event event, Collector<Event> out) throws Exception {
            if (keyHasBeenSeen.value() == null) {
                out.collect(event);
                keyHasBeenSeen.update(true);
            }
        }
    }

    public static class ControlFunction extends RichCoFlatMapFunction<String, String, String> {
        private ValueState<Boolean> blocked;

        @Override
        public void open(Configuration config) {
            blocked = getRuntimeContext()
                    .getState(new ValueStateDescriptor<>("blocked", Boolean.class));
        }

        @Override
        public void flatMap1(String control_value, Collector<String> out) throws Exception {
            blocked.update(Boolean.TRUE);
        }

        @Override
        public void flatMap2(String data_value, Collector<String> out) throws Exception {
            if (blocked.value() == null) {
                out.collect(data_value);
            }
        }
    }
}

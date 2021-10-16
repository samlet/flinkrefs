package com.bluecc.refs.functor;

import com.bluecc.refs.functor.domain.LogEntity;
import org.apache.flink.api.common.functions.AggregateFunction;

public class Aggs {
    public static class CountAgg implements AggregateFunction<LogEntity, Long, Long> {
        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(LogEntity logEntity, Long aLong) {
            return aLong + 1;
        }

        @Override
        public Long getResult(Long aLong) {
            return aLong;
        }

        @Override
        public Long merge(Long aLong, Long acc1) {
            return aLong + acc1;
        }
    }

}
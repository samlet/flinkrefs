package com.bluecc.refs.functor;

import com.bluecc.refs.functor.domain.TopProductEntity;
import org.apache.flink.api.common.functions.ReduceFunction;

public class Reduces {
    public static class TopPruductReduceFunction implements ReduceFunction<TopProductEntity> {
        @Override
        public TopProductEntity reduce(TopProductEntity t1, TopProductEntity t2) throws Exception {

            TopProductEntity top = new TopProductEntity();
            top.setProductId(t1.getProductId());
            top.setActionTimes(t1.getActionTimes() + t2.getActionTimes());
            return top;
        }
    }

}

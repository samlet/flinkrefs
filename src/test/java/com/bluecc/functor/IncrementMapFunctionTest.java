package com.bluecc.functor;

import org.apache.flink.api.common.functions.MapFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class IncrementMapFunctionTest {

    @Test
    public void testIncrement() throws Exception {
        // instantiate your function
        IncrementMapFunction incrementer = new IncrementMapFunction();

        // call the methods that you have implemented
        assertEquals(3L, incrementer.map(2L));
    }
}

class IncrementMapFunction implements MapFunction<Long, Long> {

    @Override
    public Long map(Long record) throws Exception {
        return record + 1;
    }
}

package com.bluecc.functor;
import com.google.common.collect.Lists;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class IncrementFlatMapFunctionTest {

    @Test
    public void testIncrement() throws Exception {
        // instantiate your function
        IncrementFlatMapFunction incrementer = new IncrementFlatMapFunction();

        Collector<Integer> collector = mock(Collector.class);

        // call the methods that you have implemented
        incrementer.flatMap(2, collector);

        //verify collector was called with the right output
//        Mockito.verify(collector, times(1)).collect(3);
        Mockito.verify(collector, times(1)).collect(4); // 4 is core-number
    }

    @Test
    public void testFlatMap() throws Exception {
        MyStatelessFlatMap statelessFlatMap = new MyStatelessFlatMap();
        List<String> out = new ArrayList<>();
        ListCollector<String> listCollector = new ListCollector<>(out);
        statelessFlatMap.flatMap("world", listCollector);
        assertEquals(Lists.newArrayList("hello world"), out);
    }

}

class IncrementFlatMapFunction implements FlatMapFunction<Integer, Integer> {
    @Override
    public void flatMap(Integer in, Collector<Integer> collector) throws Exception {
        Integer out=in*in;
        collector.collect(out);
    }
}

class MyStatelessFlatMap implements FlatMapFunction<String, String> {
    @Override
    public void flatMap(String in, Collector<String> collector) throws Exception {
        String out = "hello " + in;
        collector.collect(out);
    }
}

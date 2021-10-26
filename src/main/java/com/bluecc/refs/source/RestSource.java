package com.bluecc.refs.source;

import com.bluecc.refs.source.entity.Restaurant;
import kong.unirest.Unirest;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import static java.lang.Thread.sleep;

public class RestSource {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<Restaurant> dataStream = env.addSource(new RestSourceObjectFunction<>(Restaurant.class))
                // if the result is generic-type, then: .returns(new TypeHint<Tuple2<String, Double>>(){})
                .returns(Restaurant.class);
        dataStream.print();

        env.execute();
    }

    public static class RestSourceObjectFunction<T> implements SourceFunction<T> {
        Class<T> clz;

        public RestSourceObjectFunction(Class<T> clz) {
            this.clz = clz;
        }

        @Override
        public void run(SourceContext<T> ctx) throws Exception {
            // if return list, then use below definition with asObject:
            // GenericType ref = new GenericType<List<Integer>>() { };
            T ro = Unirest.get("http://localhost:8088/bot/restaurant/1")
                    .header("Content-Type", "application/json")
                    .asObject(clz)
                    .getBody();
            ctx.collect(ro);
        }

        @Override
        public void cancel() {

        }
    }
}

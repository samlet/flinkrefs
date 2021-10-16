package com.bluecc.refs.functor;

import com.google.common.collect.Maps;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

/**
 * $ just run functor.RunnerMain -run control
 */
public class RunnerMain {
    public static void main(String[] args) throws Exception {
        Map<String, Runnable> runners = Maps.newHashMap();

        runners.put("control", () -> {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            DataStream<String> control = env
                    .fromElements("DROP", "IGNORE")
                    .keyBy(x -> x);

            DataStream<String> streamOfWords = env
                    .fromElements("Apache", "DROP", "Flink", "IGNORE")
                    .keyBy(x -> x);

            control
                    .connect(streamOfWords)
                    .flatMap(new States.ControlFunction())
                    .print();

            try {
                env.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ParameterTool parameters = ParameterTool.fromArgs(args);
        String target = parameters.get("run", "control");
        Runnable runnable=runners.get(target);
        if(runnable!=null){
            runnable.run();
        }else{
            System.err.println("Run target doesn't exists: "+target);
        }
    }
}

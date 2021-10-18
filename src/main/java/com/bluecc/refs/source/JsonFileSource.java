package com.bluecc.refs.source;

import com.bluecc.refs.hotitems_analysis.beans.UserBehavior;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class JsonFileSource {
    @Data
    public static class Hotel {
        private Integer id;
        private String city;
        private String name;
    }

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> inputStream = env.readTextFile("../bluesrv/maintain/dump/hotel.jsonl");
        DataStream<Hotel> dataStream = inputStream
                .map(line -> {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                            .create();
                    return gson.fromJson(line, Hotel.class);
                });

        dataStream.print("hotel");
        env.execute();
    }
}
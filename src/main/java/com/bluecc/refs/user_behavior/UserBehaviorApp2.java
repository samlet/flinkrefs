package com.bluecc.refs.user_behavior;

import com.bluecc.fixtures.Modules;
import com.bluecc.refs.sqlflow.PrefabManager;
import com.google.inject.Injector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * $ just run sqlflow.UserBehaviorApp
 */
public class UserBehaviorApp2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        Injector injector = Modules.build();
        PrefabManager prefabManager = injector.getInstance(PrefabManager.class);

        prefabManager.defineTables(tEnv, "topcat_app",
                "user_behavior_kf", "buy_cnt_per_hour_es");

        String sql = "INSERT INTO buy_cnt_per_hour\n" +
                "SELECT HOUR(TUMBLE_START(ts, INTERVAL '1' HOUR)), COUNT(*)\n" +
                "FROM user_behavior\n" +
                "WHERE behavior = 'buy'\n" +
                "GROUP BY TUMBLE(ts, INTERVAL '1' HOUR)";

        tEnv.executeSql(sql);
//        tEnv.execute("buy_cnt_per_hour");
        env.execute("buy_cnt_per_hour");
    }
}

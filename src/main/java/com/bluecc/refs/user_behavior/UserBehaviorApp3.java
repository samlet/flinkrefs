package com.bluecc.refs.user_behavior;

import com.bluecc.fixtures.Modules;
import com.bluecc.refs.sqlflow.PrefabManager;
import com.google.inject.Injector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 统计一天中每一刻的累计独立用户数（uv），也就是每一刻的 uv 数都代表从0点到当前时刻为止的总计 uv 数
 * $ just run sqlflow.UserBehaviorApp3
 */
public class UserBehaviorApp3 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        Injector injector = Modules.build();
        PrefabManager prefabManager = injector.getInstance(PrefabManager.class);

        prefabManager.defineTables(tEnv, "topcat_app",
                "user_behavior_kf",
                "buy_cnt_per_hour_es",
                "cumulative_uv_es");

        String sql = "INSERT INTO cumulative_uv\n" +
                "SELECT date_str, MAX(time_str), COUNT(DISTINCT user_id) as uv\n" +
                "FROM (\n" +
                "  SELECT\n" +
                "    DATE_FORMAT(ts, 'yyyy-MM-dd') as date_str,\n" +
                "    SUBSTR(DATE_FORMAT(ts, 'HH:mm'),1,4) || '0' as time_str,\n" +
                "    user_id\n" +
                "  FROM user_behavior)\n" +
                "GROUP BY date_str";

        tEnv.executeSql(sql);
//        tEnv.execute("buy_cnt_per_hour");
        env.execute();
    }
}

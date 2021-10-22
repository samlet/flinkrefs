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
 * 统计每小时的成交量:
 * 统计每小时的成交量就是每小时共有多少 "buy" 的用户行为。因此会需要用到 TUMBLE 窗口函数，
 * 按照一小时切窗。然后每个窗口分别统计 "buy" 的个数，这可以通过先过滤出 "buy" 的数据，
 * 然后 COUNT(*) 实现。
 *
 * $ just run sqlflow.UserBehaviorApp2
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

package com.bluecc.refs.user_behavior;

import com.bluecc.fixtures.Modules;
import com.bluecc.refs.sqlflow.PrefabManager;
import com.google.inject.Injector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * 类目排行榜，从而了解哪些类目是支柱类目
 *
 * $ just run sqlflow.UserBehaviorApp4
 */
public class UserBehaviorApp4 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        Injector injector = Modules.build();
        PrefabManager prefabManager = injector.getInstance(PrefabManager.class);

        prefabManager.defineTables(tEnv, "topcat_app",
                "user_behavior_kf",
                "category_dim_sql",
                "top_category_es",
                "rich_user_behavior_v");

        String sql = "INSERT INTO top_category\n" +
                "SELECT category_name, COUNT(*) buy_cnt\n" +
                "FROM rich_user_behavior\n" +
                "WHERE behavior = 'buy'\n" +
                "GROUP BY category_name";

        tEnv.executeSql(sql);
        env.execute();
    }
}

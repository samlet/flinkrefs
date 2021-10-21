package com.bluecc.refs.sqlflow;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.FileNotFoundException;
import java.util.Map;

@Singleton
public class PrefabManager {
    private static final Logger logger = LoggerFactory.getLogger(PrefabManager.class);
    Map<String, Prefabs.TablesElement> prefabs= Maps.newConcurrentMap();
    Jinjava jinjava = new Jinjava();

    public PrefabManager(){

    }

    /**
     * execute(tEnv, "source_kafka", "user_info_input")
     *
     * @param tEnv
     * @param asset
     * @param descriptor
     * @return
     * @throws FileNotFoundException
     */
    public TableResult define(StreamTableEnvironment tEnv, String asset, String descriptor) throws FileNotFoundException {

        Prefabs.TablesElement tablesElement=prefabs.get(asset);
        if(tablesElement==null) {
            tablesElement=new Prefabs().load("assets/" + asset+".yml");
            prefabs.put(asset, tablesElement);
        }

        String sql=tablesElement.getTables().get(descriptor).create;
        Map<String, Object> context = Maps.newHashMap();
        context.put("name", descriptor);
        String renderedSql = jinjava.render(sql, context);
        logger.info(renderedSql);

        return tEnv.executeSql(renderedSql);
    }
}

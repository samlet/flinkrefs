package com.bluecc.refs.sqlflow;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class PrefabsTest {
    @Test
    public void testLoadAssets() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream("assets/source_kafka.yml");
        Map<String, Object> obj = yaml.load(inputStream);
//        System.out.println(obj);
        processTables((Map<String,Object>)obj.get("tables"));
    }

    private void processTables(Map<String,Object> tables) {
        System.out.println(tables.keySet());
        processTable((Map<String,Object>)tables.get("user_page_input"));
    }

    private void processTable(Map<String, Object> table) {
        if(table.containsKey("create")) {
            String content = (String) table.get("create");
            System.out.println(content);
        }
        if(table.containsKey("query")){
            // ...
        }
    }

    @Test
    public void testLoadConfig() throws FileNotFoundException {
        Yaml yaml = new Yaml(new Constructor(Prefabs.TablesElement.class));
        InputStream inputStream = new FileInputStream("assets/source_kafka.yml");
        Prefabs.TablesElement prefabs = yaml.load(inputStream);
        System.out.println(prefabs);

        inputStream = new FileInputStream("assets/source_simple.yml");
        prefabs = yaml.load(inputStream);
        System.out.println(prefabs);
    }

    @Test
    public void testLoadPrefabs() throws FileNotFoundException {
        Prefabs.TablesElement tablesElement=new Prefabs().load("assets/source_simple.yml");
        System.out.println(tablesElement.getTables().keySet());
        assertFalse(tablesElement.getTables().isEmpty());
    }
}


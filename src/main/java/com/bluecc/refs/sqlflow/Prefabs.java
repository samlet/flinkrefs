package com.bluecc.refs.sqlflow;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prefabs {
    @Data
    public static class ExecutorElement{
        List<String> vars=new ArrayList<>();
        String sql;
    }
    @Data
    public static class TableElement{
        String create;
        Map<String, ExecutorElement> query=new HashMap<>();
        Map<String, ExecutorElement> sink=new HashMap<>();
    }

    @Data
    public static class PipeElement{
        String sql;
    }
    @Data
    public static class TablesElement{
        String version;
        Map<String, TableElement> tables=new HashMap<>();
        Map<String, PipeElement> pipes=new HashMap<>();
    }

    public TablesElement load(String file) throws FileNotFoundException {
        Yaml yaml = new Yaml(new Constructor(Prefabs.TablesElement.class));
        InputStream inputStream = new FileInputStream(file);
        Prefabs.TablesElement prefabs = yaml.load(inputStream);
        return prefabs;
    }
}


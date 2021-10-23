package com.bluecc.refs.recommend.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IStore {
    void createTable(String tableName, String... columnFamilies) throws IOException;

    String getData(String tableName, String rowKey, String famliyName, String column) throws IOException;

    List<Map.Entry<String, Object>> getRow(String tableName, String rowKey) throws IOException;

    void putData(String tablename, String rowkey, String famliyname, String column, String data) throws IOException;

    void increamColumn(String tablename, String rowkey, String famliyname, String column) throws IOException;

    List<String> getAllKey(String tableName) throws IOException;
}

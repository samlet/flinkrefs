package com.bluecc.refs.async;

import com.alibaba.fastjson.JSONObject;

public interface IDimSource {
    JSONObject getDimInfo(String tableName, String id);
}



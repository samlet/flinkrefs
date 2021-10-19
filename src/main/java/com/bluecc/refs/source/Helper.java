package com.bluecc.refs.source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class Helper {
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .create();
}


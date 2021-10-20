package com.bluecc.refs.generator;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.bluecc.refs.source.Helper.GSON;

public class DataSets {
    public static List<SkuInfo> SkuInfos() throws IOException {
        List<String> lines = IOUtils.readLines(new FileReader("../bluesrv/maintain/dump/sku_info.jsonl"));
        List<SkuInfo> skuInfos = lines.stream()
                .map(l -> GSON.fromJson(l, SkuInfo.class))
                .collect(Collectors.toList());
        return skuInfos;
    }

    public static List<SpuInfo> SpuInfos() throws IOException {
        List<String> lines = IOUtils.readLines(new FileReader("../bluesrv/maintain/dump/spu_info.jsonl"));
        List<SpuInfo> rs = lines.stream()
                .map(l -> GSON.fromJson(l, SpuInfo.class))
                .collect(Collectors.toList());
        return rs;
    }
}

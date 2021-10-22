package com.bluecc.refs.generator;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.bluecc.refs.source.Helper.GSON;
import static org.junit.Assert.*;

public class SkuInfoTest {

    @Test
    public void getSkuInfo() throws IOException {
        List<String> lines=IOUtils.readLines(new FileReader("../bluesrv/maintain/dump/sku_info.jsonl"));
        List<SkuInfo> skuInfos=lines.stream().map(l -> GSON.fromJson(l, SkuInfo.class)).collect(Collectors.toList());
        skuInfos.forEach(sku -> System.out.println(sku));
    }
}


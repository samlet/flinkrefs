package com.bluecc.fixtures;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Singleton
public class PropertyFac {

    private final static String CONF_NAME = "config.properties";

    private final Properties contextProperties;

    PropertyFac() throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONF_NAME);
        if(in==null){
            throw new IOException("Cannot load config: "+CONF_NAME);
        }
        contextProperties = new Properties();
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        contextProperties.load(inputStreamReader);
    }

    public String getStrValue(String key) {
        return contextProperties.getProperty(key);
    }

    public int getIntValue(String key) {
        String strValue = getStrValue(key);
        // 注意，此处没有做校验，暂且认为不会出错
        return Integer.parseInt(strValue);
    }

    public Properties getKafkaProperties(String groupId) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", getStrValue("kafka.bootstrap.servers"));
        properties.setProperty("zookeeper.connect", getStrValue("kafka.zookeeper.connect"));
        properties.setProperty("group.id", groupId);
        return properties;
    }

}
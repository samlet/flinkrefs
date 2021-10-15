package com.bluecc.fixtures;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisProcs {
    public static void main(String[] args) {
        try (Jedis jedis = Modules.build().getInstance(RedisFac.class).getResource()) {

            jedis.set("events/city/rome", "32,15,223,828");
            String cachedResponse = jedis.get("events/city/rome");
            System.out.println(cachedResponse);

            jedis.lpush("queue#tasks", "firstTask");
            jedis.lpush("queue#tasks", "secondTask");

            String task = jedis.rpop("queue#tasks");
            while (task != null) {
                System.out.println(task);
                task = jedis.rpop("queue#tasks");
            }
        }
    }
}

/*
⊕ [Intro to Jedis - the Java Redis Client Library | Baeldung](https://www.baeldung.com/jedis-java-redis-client-library)

 */


package com.sin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisOperator {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public long ttl(String key){
        return
    }
}

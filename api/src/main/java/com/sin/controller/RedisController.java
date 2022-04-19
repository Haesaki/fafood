package com.sin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping
    public Object set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        return "ok";
    }
}

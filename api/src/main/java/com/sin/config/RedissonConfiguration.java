package com.sin.config;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

@Configuration
public class RedissonConfiguration {

    @Autowired
    Environment env;

    @Bean
    public RedissonClient redissonClient() {
        String host = env.getProperty("spring.redis.host");
        String port = env.getProperty("spring.redis.port");
        String password = env.getProperty("spring.redis.password");

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        return Redisson.create(config);
    }
}

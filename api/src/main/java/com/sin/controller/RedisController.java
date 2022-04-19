package com.sin.controller;

import com.sin.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

    private RedisTemplate redisTemplate;

    private RedisOperator redisOperator;

    public RedisController(RedisTemplate redisTemplate, RedisOperator redisOperator) {
        this.redisTemplate = redisTemplate;
        this.redisOperator = redisOperator;
    }

    @GetMapping("/set")
    public Object set(String key, String value) {
        redisOperator.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(String key) {
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key) {
        redisOperator.del(key);
        return "OK";
    }

    // 大量key查询
    @GetMapping("/getALot")
    public Object getALot(String... keys) {
        List<String> resultl = new ArrayList<>();
        for (String k : keys) {
            resultl.add(redisOperator.get(k));
        }
        return resultl;
    }

    // 批量查询
    @GetMapping("/mget")
    public Object mget(String... keys){
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.mget(keysList);
    }

    @GetMapping("/batchGet")
    public Object batchGet(String... keys) {
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.batchGet(keysList);
    }
}

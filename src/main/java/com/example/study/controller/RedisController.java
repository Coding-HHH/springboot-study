package com.example.study.controller;


import com.example.study.entity.Entity;
import com.example.study.utils.reidsUtils.RedisUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RedisController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("redis")
    public String redis(){
        redisTemplate.opsForValue().set("888",new Entity("666","huang",123));
        return "success";
    }
}

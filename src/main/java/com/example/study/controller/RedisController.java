package com.example.study.controller;


import com.example.study.entity.Entity;
import com.example.study.utils.reidsUtils.RedisUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
public class RedisController {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("redis")
    public void redis(){
        for (int i = 0; i < 2; i++) {
            new Thread(()->{
                try {
                    while (!redisUtils.lock("test","147258369",3,redisTemplate)){
                        System.out.println(Thread.currentThread().getName() +"尝试获取锁");
                    }
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() +"执行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    redisTemplate.delete("test");
                    System.out.println(Thread.currentThread().getName() +"释放锁");
                }
            }).start();
        }
    }
}

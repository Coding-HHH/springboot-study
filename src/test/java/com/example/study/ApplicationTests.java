package com.example.study;

import com.example.study.utils.reidsUtils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class ApplicationTests {


    @Autowired
    public  RedisUtils redisUtils ;

    @Test
    public void test() {
        redisUtils.set("","test","value");
    }


}

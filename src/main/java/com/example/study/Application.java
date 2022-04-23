package com.example.study;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *  参考； https://zhuanlan.zhihu.com/p/414028184
 *  github的token：  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
 *  把token添加远程仓库链接中：
 *  git remote set-url origin https://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@github.com/Coding-HHH/springboot-study.git/
 *
 */

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan({"com.example.redis.*"})
@EnableCaching
@EnableScheduling
@EnableAsync
@EnableEncryptableProperties
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

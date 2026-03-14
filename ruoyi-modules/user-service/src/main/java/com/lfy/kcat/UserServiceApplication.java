package com.lfy.kcat;

import org.dromara.common.mybatis.config.MybatisPlusConfiguration;
import org.dromara.common.satoken.config.SaTokenConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@MapperScan(basePackages = "com.lfy.kcat.user.mapper")
@SpringBootApplication(exclude = {SaTokenConfiguration.class,
        MybatisPlusConfiguration.class})


public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class,args);
        System.out.println("(♥◠‿◠)ﾉﾞ  用户服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

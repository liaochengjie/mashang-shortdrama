package com.lfy.kcat.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients

@MapperScan("com.lfy.kcat.content.mapper")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceHealthContributorAutoConfiguration.class})
public class ContentServiceApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ContentServiceApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  内容服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

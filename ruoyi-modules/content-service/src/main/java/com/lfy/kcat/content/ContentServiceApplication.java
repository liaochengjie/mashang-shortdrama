package com.lfy.kcat.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ContentServiceApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ContentServiceApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  内容服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

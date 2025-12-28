package com.lfy.kcat.user.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SaTokenConfig {

    /**
     * 总共有三种模式
     * 1：simple简单
     * 2：Mixin混入
     * 3：StateLess
     * 最常用的是simple，因为具备功能较多
     * @return
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }


    /**
     * 自定义令牌
     */
    @PostConstruct
    public void rewriteSaStrategy(){
        SaStrategy.instance.createToken = (loginId, loginType) -> {
            log.info("loginId={}, loginType={}", loginId, loginType);
            return "lcjJWT_"+ SaFoxUtil.getRandomString(16)+"_"+loginId;    // 随机60位长度字符串
        };
    }
}

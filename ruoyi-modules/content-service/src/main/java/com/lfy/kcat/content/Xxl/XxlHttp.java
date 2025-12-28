package com.lfy.kcat.content.Xxl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.lfy.kcat.content.config.XxlJobConfig;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class XxlHttp {

    @Autowired
    XxlJobConfig xxlJobConfig;

    /**
     * 机器人登录xxl-job
     * @return
     */
    public HttpCookie mockLogin(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("userName",xxlJobConfig.getUserName());
        map.put("password",xxlJobConfig.getPassword());
        map.put("ifRemember",xxlJobConfig.getIfRemember());
        log.info("{}正在模拟登录xxl-job",xxlJobConfig.getUserName());
        //发送请求模拟xxlJob登录请求
        HttpResponse execute = HttpUtil.createPost(xxlJobConfig.getAdminAddresses() + "/auth/doLogin")
            .form(map)
            .execute();
        //获取到用户登录的令牌
        List<HttpCookie> cookies = execute.getCookies();
        log.info("{}已经成功获取令牌，令牌为:{}",xxlJobConfig.getUserName(),cookies.get(0));
       return cookies.get(0);
    }

    /**
     * 模拟实现任务调用
     * @param httpCookie
     * @param map
     * @return
     */
    public HttpResponse traggerJob(HttpCookie httpCookie,
                                   Map<String,Object> map){
        HttpResponse execute = HttpUtil.createPost(xxlJobConfig.getAdminAddresses() + "/jobinfo/trigger")
            .form(map)
            .cookie(httpCookie)
            .execute();
        log.info("正在调用的任务ID为：{}",map.get("id"));
        return execute;

    }
}

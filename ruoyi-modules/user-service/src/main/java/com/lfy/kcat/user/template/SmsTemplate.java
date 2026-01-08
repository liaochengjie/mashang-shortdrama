package com.lfy.kcat.user.template;

import cn.hutool.http.HttpUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsTemplate {

    private String baseUrl;
    private String method;
    private String appcode;

    public void sendSmsCode(String phone,String code){
        Map<String, Object> map = new HashMap<>();
        map.put("phone_number",phone);
        map.put("content","code:"+code);
        map.put("template_id","CST_ptdie100");
        HttpUtil.createPost(baseUrl)
            .header("Authorization", "APPCODE " + appcode)
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .form(map)
            .execute();
    }
}

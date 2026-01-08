package com.lfy.kcat.user.business.impl;


import com.lfy.kcat.user.business.VerifyCodeBizService;
import com.lfy.kcat.user.template.SmsTemplate;
import com.lfy.kcat.user.constant.BizConst;
import com.lfy.kcat.user.constant.RedisConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerifyCodeBizServiceImpl implements VerifyCodeBizService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SmsTemplate smsTemplate;
    /**
     * 发送验证码服务
     * @param phone
     */
    @Override
    public void sendCode(String phone) {
        //1：发送一个随机六位的验证码
        //TODO 由于那里面没钱了，所以现在用固定的来代替
        //String code = RandomUtil.randomString(6);
        String code ="060717";
        //通过短信API来发送给手机
        smsTemplate.sendSmsCode(phone,code);

        //2：将电话号码喝短信数据存入redis入库
        redisTemplate.opsForValue().set(
            RedisConst.POHNE_CODE_KEY+phone,
            code,
            BizConst.DEFAULT_CODE_EXPIRES,
            TimeUnit.SECONDS);


    }

    /**
     * 校验验证码服务
     *
     * @param phone
     * @param code
     * @return
     */
    @Override
    public boolean codeAuth(String phone, String code) {
        String redisCode = redisTemplate.opsForValue().get(RedisConst.POHNE_CODE_KEY + phone);
        boolean equals = code.equals(redisCode);
        return equals;
    }
}

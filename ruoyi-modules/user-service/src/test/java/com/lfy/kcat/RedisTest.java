package com.lfy.kcat;

import com.lfy.kcat.user.template.SmsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class RedisTest {

    @Autowired
    SmsTemplate smsTemplate;

    @Test
    void test01(){
        smsTemplate.sendSmsCode("13418862370","123456");
    }
}

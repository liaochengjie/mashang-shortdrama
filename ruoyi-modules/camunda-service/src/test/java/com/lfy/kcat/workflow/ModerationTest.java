package com.lfy.kcat.workflow;

import com.lfy.kcat.workflow.ai.OllamaModerationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ModerationTest {
    @Autowired
    OllamaModerationService ollamaModerationService;
    @Test
    public void test01(){
        String result = ollamaModerationService.moderation("我对象要打我");
        System.out.println(result);
    }
}

package com.lfy.kcat.content.ai.Controller;

import org.springframework.ai.moderation.Generation;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.openai.OpenAiModerationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openAi")
public class ModerationController {

    @Autowired
    OpenAiModerationModel openAiModerationModel;

    @GetMapping("moderation")
    Generation test(@RequestParam("msg") String msg){
        ModerationPrompt moderationPrompt = new ModerationPrompt(msg);
        Generation result = openAiModerationModel.call(moderationPrompt).getResult();
        return result;
    }
}

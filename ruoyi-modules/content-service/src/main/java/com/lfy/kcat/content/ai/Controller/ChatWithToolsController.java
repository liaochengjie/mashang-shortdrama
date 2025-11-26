package com.lfy.kcat.content.ai.Controller;

import com.lfy.kcat.content.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tools")
public class ChatWithToolsController {
    @Autowired
    OpenAiChatModel openAiChatModel;
    @Autowired
    DateTimeTools dateTimeTools;

    @GetMapping(value = "/chat")
    String chat(@RequestParam("msg") String msg) {
        String content = ChatClient.create(openAiChatModel)
            .prompt(msg)
            .tools(dateTimeTools)
            .call()
            .content();
        return content;
    }

}

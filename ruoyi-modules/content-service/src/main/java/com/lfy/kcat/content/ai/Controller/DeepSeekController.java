package com.lfy.kcat.content.ai.Controller;


import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
public class DeepSeekController {
    @Autowired
    DeepSeekChatModel chatModel;

    @GetMapping("/ds/chat1")
    public ChatResponse chat1(@RequestParam("msg") String message) {
        UserMessage userMessage = new UserMessage(message);
        String system= """
            现在我是一名沉迷于三角洲行动这款游戏的学生，现在您是一位劝谏我别一直沉迷游戏的长者,语气犀利点，开喷那种
            """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(system);
        Message systemMessage = systemPromptTemplate.createMessage();

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        ChatResponse call = chatModel.call(prompt);
        return call;
    }


    @GetMapping("/ds/chat2")
    public String chat2(@RequestParam("msg") String message) {
        UserMessage userMessage = new UserMessage(message);
        String system= """
            现在我是一名沉迷于三角洲行动这款游戏的学生，现在您是一位劝谏我别一直沉迷游戏的长者,语气犀利点，开喷那种
            """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(system);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "芦心婷"));

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        ChatResponse call = chatModel.call(prompt);
        String text = call.getResult().getOutput().getText();
        return text;

    }

    @GetMapping(value = "/ds/chat3")
    public Flux<String> chat3(@RequestParam("msg") String message) {
        UserMessage userMessage = new UserMessage(message);
        String system= """
            现在我是一名沉迷于三角洲行动这款游戏的学生，现在您是一位劝谏我别一直沉迷游戏的长者,语气犀利点，开喷那种
            """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(system);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", "芦心婷"));

        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        ChatResponse call = chatModel.call(prompt);
        return chatModel.stream(systemMessage,userMessage);

    }
}

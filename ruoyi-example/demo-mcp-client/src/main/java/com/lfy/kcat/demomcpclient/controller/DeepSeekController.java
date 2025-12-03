package com.lfy.kcat.demomcpclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DeepSeekController {

    private final ChatClient chatClient;

    public DeepSeekController(ChatClient.Builder chatClientBuilder,
                              ToolCallbackProvider toolCallbackProvider) {
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        chatClient= chatClientBuilder.defaultToolCallbacks(toolCallbacks).build();
    }

    @GetMapping(value="/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat01(@RequestParam("msg") String msg) {
        Flux<String> content = chatClient.prompt(msg).stream().content();
        return content;

    }
}

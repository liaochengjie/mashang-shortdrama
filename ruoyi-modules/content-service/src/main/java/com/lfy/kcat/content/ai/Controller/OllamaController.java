package com.lfy.kcat.content.ai.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/Ollama")
public class OllamaController {
    @Autowired
    OllamaChatModel ollamaChatModel;


    @GetMapping("/chat")
    public String chat01(@RequestParam("msg") String message){
        String content = ChatClient.builder(ollamaChatModel)
            .build().prompt(message).call().content();
        return content;
    }
}

package com.lfy.kcat.workflow.ai.impl;

import com.lfy.kcat.workflow.ai.OllamaModerationService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OllamaModerationServiceImpl implements OllamaModerationService {

    @Autowired
    OllamaChatModel ollamaChatModel;
    /**
     * 用于大模型回复情感审核的
     * @param text
     * @return
     */
    public String moderation(String text){
        UserMessage userMessage = new UserMessage(text);
        String systemtext="接下来我会给你发一段文本，需要你分析一下这个文本是正向还是负面的文本。直接返回1或者0，1代表正向文本，0代表负面文本，不要返回其他废话";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemtext);
        Message systemMessage = systemPromptTemplate.createMessage();
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String resultText = ollamaChatModel.call(prompt)
            .getResult()
            .getOutput()
            .getText();
        return resultText;
    }
}

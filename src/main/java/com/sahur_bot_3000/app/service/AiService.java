package com.sahur_bot_3000.app.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {
    @Value("${openai.api.key}")
    private String apiKey;

    public String generateResponse(String prompt) {
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(1000)
                .build();

        return service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
    }
}
package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.model.Enums.HackathonType;
import dev.ai4j.openai4j.chat.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiService {

    private final ChatClient chatClient = ChatClient.builder()
            .apiKey("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")
            .build();

    public String[] generateTheme(HackathonType type) {
        String prompt = String.format("""
            Generează o temă interesantă pentru un hackathon de tip %s.
            Returnează doar titlul și o descriere interesanta de 2-3 linii care e destul de captivanta si ofera participantilor libertate sa faca  ceva divers si interesant, separate de `||`.
            Ex: „Nume temă” || „Descriere scurtă”
            """, type.name());

        ChatRequest request = ChatRequest.builder()
                .messages(
                        List.of(
                                new SystemMessage("Ești un organizator de hackathoane creative pe teme de tip fotografie, audio si text."),
                                new UserMessage(prompt)
                        )
                )
                .model("gpt-3.5-turbo")
                .temperature(0.8)
                .build();

        ChatResponse response = chatClient.chatCompletion(request);
        String content = response.content();
        return content.split("\\|\\|");
    }
}
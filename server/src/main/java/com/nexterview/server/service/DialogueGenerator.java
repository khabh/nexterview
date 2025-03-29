package com.nexterview.server.service;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
public class DialogueGenerator {

    private final ChatClient chatClient;

    public DialogueGenerator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("당신은 개발자 면접을 준비를 위해 한국어로 3개의 예상 질문과 각 질문에 대한 예시 답변을 생성해 주는 역할입니다.")
                .build();
    }

    public List<GeneratedDialogueDto> generate(CustomizedPrompt customizedPrompt) {
        String rawPrompt = customizedPrompt.getRawPrompt();
        return chatClient.prompt()
                .user(rawPrompt)
                .call()
                .entity(new ParameterizedTypeReference<>() {
                });
    }
}

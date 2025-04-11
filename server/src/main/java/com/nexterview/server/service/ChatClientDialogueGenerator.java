package com.nexterview.server.service;

import com.nexterview.server.domain.CustomizedPrompt;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.service.dto.response.GeneratedDialogueDto;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
public class ChatClientDialogueGenerator implements DialogueGenerator {

    private final ChatClient chatClient;

    public ChatClientDialogueGenerator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("당신은 개발자 면접을 준비를 위해 한국어로 3개의 예상 질문과 각 질문에 대한 예시 답변을 생성해 주는 역할입니다.")
                .build();
    }

    public GeneratedDialogues generate(CustomizedPrompt customizedPrompt) {
        try {
            String rawPrompt = customizedPrompt.getRawPrompt();
            ResponseEntity<ChatResponse, List<GeneratedDialogueDto>> chatResponse = chatClient.prompt()
                    .user(rawPrompt)
                    .call()
                    .responseEntity(new ParameterizedTypeReference<>() {
                    });
            return createGeneratedDialogues(chatResponse);
        } catch (Exception e) {
            throw new NexterviewException(NexterviewErrorCode.CHAT_API_UNAVAILABLE);
        }
    }

    private static GeneratedDialogues createGeneratedDialogues(
            ResponseEntity<ChatResponse, List<GeneratedDialogueDto>> chatResponse
    ) {
        if (chatResponse.response() == null) {
            throw new NexterviewException(NexterviewErrorCode.CHAT_API_UNAVAILABLE);
        }
        Integer totalTokens = chatResponse.response()
                .getMetadata()
                .getUsage()
                .getTotalTokens();

        return new GeneratedDialogues(totalTokens, chatResponse.entity());
    }
}

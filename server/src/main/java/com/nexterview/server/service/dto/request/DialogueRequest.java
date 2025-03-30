package com.nexterview.server.service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DialogueRequest(
        @NotBlank(message = "문답의 질문은 필수입니다.")
        String question,
        String answer
) {
}

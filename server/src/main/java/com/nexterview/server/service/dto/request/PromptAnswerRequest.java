package com.nexterview.server.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record PromptAnswerRequest(
        @NotNull(message = "프롬프트 쿼리 ID는 필수입니다.")
        Long promptQueryId,

        @NotBlank(message = "프롬프트 답변은 공백일 수 없습니다.")
        String answer
) {
}

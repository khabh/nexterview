package com.nexterview.server.service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import javax.validation.constraints.NotNull;

public record GenerateDialoguesRequest(
        @NotNull(message = "프롬프트 ID는 필수입니다.")
        Long promptId,

        @NotEmpty(message = "프롬프트 질문에 대한 답변은 필수입니다.")
        List<PromptAnswerRequest> promptAnswers
) {
}

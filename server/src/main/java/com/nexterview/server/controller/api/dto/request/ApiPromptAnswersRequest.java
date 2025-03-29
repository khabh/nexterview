package com.nexterview.server.controller.api.dto.request;

import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ApiPromptAnswersRequest(
        @Valid
        @NotEmpty(message = "프롬프트 답변은 필수입니다.")
        List<PromptAnswerRequest> promptAnswers
) {
}

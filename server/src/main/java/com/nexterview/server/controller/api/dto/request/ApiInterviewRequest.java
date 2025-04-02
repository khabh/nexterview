package com.nexterview.server.controller.api.dto.request;

import com.nexterview.server.service.dto.request.DialogueRequest;
import com.nexterview.server.service.dto.request.PromptAnswerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ApiInterviewRequest(
        @NotBlank(message = "인터뷰 제목은 필수입니다.")
        String title,

        @Valid
        List<PromptAnswerRequest> promptAnswers,

        @Valid
        @NotEmpty(message = "인터뷰 문답 목록은 필수입니다.")
        List<DialogueRequest> dialogues
) {
}

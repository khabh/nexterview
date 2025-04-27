package com.nexterview.server.service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import javax.validation.constraints.NotNull;

public record GuestInterviewRequest(
        @NotBlank(message = "인터뷰 제목은 필수입니다.")
        String title,

        @NotNull(message = "프롬프트 ID는 필수입니다.")
        Long promptId,

        @NotNull(message = "게스트 인터뷰 비밀번호는 필수입니다.")
        String guestPassword,

        @Valid
        List<PromptAnswerRequest> promptAnswers,

        @Valid
        @NotEmpty(message = "인터뷰 문답 목록은 필수입니다.")
        List<DialogueRequest> dialogues
) {
}

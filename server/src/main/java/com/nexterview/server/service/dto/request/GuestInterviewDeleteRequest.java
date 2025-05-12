package com.nexterview.server.service.dto.request;

import jakarta.validation.constraints.NotNull;

public record GuestInterviewDeleteRequest(
        @NotNull(message = "인터뷰 ID는 필수입니다.")
        Long interviewId,

        @NotNull(message = "비밀번호는 필수입니다.")
        String password
) {
}

package com.nexterview.server.service.dto.request;

import jakarta.validation.constraints.NotNull;

public record InterviewPasswordRequest(
        @NotNull(message = "비밀번호는 필수입니다.")
        String password
) {
}

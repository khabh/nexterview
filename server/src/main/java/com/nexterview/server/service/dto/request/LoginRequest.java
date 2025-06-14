package com.nexterview.server.service.dto.request;

import javax.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull(message = "이메일은 필수입니다.")
        String email,

        @NotNull(message = "비밀번호는 필수입니다.")
        String password
) {
}

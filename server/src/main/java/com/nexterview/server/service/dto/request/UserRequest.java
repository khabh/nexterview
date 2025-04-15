package com.nexterview.server.service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}

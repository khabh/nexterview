package com.nexterview.server.service.dto.response;

import com.nexterview.server.domain.User;

public record UserDto(
        Long id,
        String email,
        String nickname
) {

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getNickname());
    }
}

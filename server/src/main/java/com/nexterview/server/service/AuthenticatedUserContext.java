package com.nexterview.server.service;

import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.UserRepository;
import com.nexterview.server.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserContext {

    private final UserRepository userRepository;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No authenticated user found");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    public User getUser() {
        Long userId = getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.USER_NOT_FOUND));
    }
}

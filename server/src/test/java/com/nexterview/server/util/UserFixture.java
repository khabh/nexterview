package com.nexterview.server.util;

import com.nexterview.server.domain.User;
import com.nexterview.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFixture {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User getSavedUser(String email, String nickname, String rawPassword) {
        User user = User.of(email, nickname, rawPassword, passwordEncoder);
        return userRepository.save(user);
    }

    public User getAuthenticatedUser(String email, String nickname, String rawPassword) {
        User user = getSavedUser(email, nickname, rawPassword);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), rawPassword));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        return user;
    }
}


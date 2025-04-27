package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.nexterview.server.domain.User;
import com.nexterview.server.repository.UserRepository;
import com.nexterview.server.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class AuthenticatedUserContextTest {

    @Autowired
    private AuthenticatedUserContext authenticatedUserContext;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        databaseCleaner.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void 인증된_유저_아이디를_올바르게_조회한다() {
        User authenticatedUser = User.of("abcd@gmail.com", "abcd", "potato!123", passwordEncoder);
        userRepository.save(authenticatedUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticatedUser.getEmail(), "potato!123"));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long userId = authenticatedUserContext.getUserId();

        assertThat(userId).isEqualTo(authenticatedUser.getId());
    }

    @Test
    void 인증된_유저를_올바르게_조회한다() {
        User authenticatedUser = User.of("abcd@gmail.com", "abcd", "potato!123", passwordEncoder);
        userRepository.save(authenticatedUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticatedUser.getEmail(), "potato!123"));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = authenticatedUserContext.getUser();

        assertThat(user.getId()).isEqualTo(authenticatedUser.getId());
        assertThat(user.getEmail()).isEqualTo(authenticatedUser.getEmail());
    }

    @Test
    void 인증된_유저가_없으면_예외를_던진다() {
        assertThatCode(authenticatedUserContext::getUser)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No authenticated user found");
    }
}

package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.UserRepository;
import com.nexterview.server.security.CustomUserDetails;
import com.nexterview.server.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CustomUserDetailsServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    @Test
    void 유저를_조회한다() {
        String email = "test@example.com";
        String password = "password!";
        String nickname = "nickname";
        User user = User.of(email, nickname, password, passwordEncoder);
        userRepository.save(user);

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void 유저가_없으면_예외를_던진다() {
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent@example.com"))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.USER_NOT_FOUND.getMessage());
    }
}

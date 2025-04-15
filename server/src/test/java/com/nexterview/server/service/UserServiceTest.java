package com.nexterview.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.UserRepository;
import com.nexterview.server.service.dto.request.UserRequest;
import com.nexterview.server.service.dto.response.UserDto;
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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }

    @Test
    void 유저를_저장한다() {
        UserRequest request = new UserRequest("test@email.com", "테스트유저", "password1234");

        UserDto result = userService.saveUser(request);

        assertThat(result.email()).isEqualTo("test@email.com");
        assertThat(result.nickname()).isEqualTo("테스트유저");

        User saved = userRepository.findByEmail("test@email.com").orElseThrow();
        assertThat(passwordEncoder.matches("password1234", saved.getPassword())).isTrue();
    }

    @Test
    void 이메일이_중복되면_예외를_던진다() {
        User existing = User.of("test@email.com", "기존유저", "password1234", passwordEncoder);
        userRepository.save(existing);

        UserRequest duplicated = new UserRequest("test@email.com", "새로운유저", "newpassword");

        assertThatThrownBy(() -> userService.saveUser(duplicated))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining("이미 가입된 이메일입니다");
    }
}

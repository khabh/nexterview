package com.nexterview.server.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void 유효한_입력으로_유저를_생성할_수_있다() {
        String email = "test@example.com";
        String nickname = "홍길동";
        String rawPassword = "securePassword123";

        User user = User.of(email, nickname, rawPassword, encoder);

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(encoder.matches(rawPassword, user.getPassword())).isTrue();
    }

    @ParameterizedTest
    @MethodSource("잘못된_이메일_목록")
    void 이메일이_유효하지_않으면_예외가_발생한다(String invalidEmail) {
        assertThatThrownBy(() ->
                User.of(invalidEmail, "홍길동", "securePassword123", encoder)
        ).isInstanceOf(NexterviewException.class);
    }

    static Stream<String> 잘못된_이메일_목록() {
        return Stream.of(
                null,
                "",
                "     ",
                "invalidemail",
                "test@",
                "@example.com",
                "test@example",
                "test@.com",
                "test@com.",
                "test@@example.com"
        );
    }

    @ParameterizedTest
    @MethodSource("잘못된_닉네임_목록")
    void 닉네임이_유효하지_않으면_예외가_발생한다(String invalidNickname) {
        assertThatThrownBy(() ->
                User.of("test@example.com", invalidNickname, "securePassword123", encoder)
        ).isInstanceOf(NexterviewException.class);
    }

    static Stream<String> 잘못된_닉네임_목록() {
        return Stream.of(
                null,
                "",
                " ",
                "A",
                "a".repeat(21)
        );
    }

    @ParameterizedTest
    @MethodSource("잘못된_비밀번호_목록")
    void 비밀번호가_유효하지_않으면_예외가_발생한다(String invalidPassword) {
        assertThatThrownBy(() ->
                User.of("test@example.com", "홍길동", invalidPassword, encoder)
        ).isInstanceOf(NexterviewException.class);
    }

    static Stream<String> 잘못된_비밀번호_목록() {
        return Stream.of(
                null,
                "",
                "     ",
                "short",
                "a".repeat(51)
        );
    }
}

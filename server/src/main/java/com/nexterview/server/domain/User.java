package com.nexterview.server.domain;

import com.nexterview.server.domain.validator.StringInvalidException;
import com.nexterview.server.domain.validator.StringValidator;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    private static final StringValidator EMAIL_VALIDATOR = StringValidator.patternWithRange(5, 100,
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final StringValidator NICKNAME_VALIDATOR = StringValidator.notEmptyWithRange(2, 20);
    private static final StringValidator PASSWORD_VALIDATOR = StringValidator.notEmptyWithRange(8, 50);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private String password;

    private User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public static User of(String email, String nickname, String rawPassword, PasswordEncoder passwordEncoder) {
        validateEmail(email);
        validateNickname(nickname);
        validateRawPassword(rawPassword);

        return new User(email, nickname, passwordEncoder.encode(rawPassword));
    }

    private static void validateEmail(String email) {
        try {
            EMAIL_VALIDATOR.validate(email);
        } catch (StringInvalidException e) {
            throw new NexterviewException(NexterviewErrorCode.EMAIL_INVALID, email);
        }
    }

    private static void validateNickname(String nickname) {
        try {
            NICKNAME_VALIDATOR.validate(nickname);
        } catch (StringInvalidException e) {
            throw new NexterviewException(NexterviewErrorCode.NICKNAME_INVALID, nickname);
        }
    }

    private static void validateRawPassword(String rawPassword) {
        try {
            PASSWORD_VALIDATOR.validate(rawPassword);
        } catch (StringInvalidException e) {
            throw new NexterviewException(NexterviewErrorCode.PASSWORD_INVALID, rawPassword);
        }
    }
}

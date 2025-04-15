package com.nexterview.server.service;

import com.nexterview.server.domain.User;
import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.UserRepository;
import com.nexterview.server.service.dto.request.UserRequest;
import com.nexterview.server.service.dto.response.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto saveUser(UserRequest userRequest) {
        validateEmailUnique(userRequest.email());
        User user = User.of(
                userRequest.email(),
                userRequest.nickname(),
                userRequest.password(),
                passwordEncoder
        );
        userRepository.save(user);

        return UserDto.of(user);
    }

    private void validateEmailUnique(String email) {
        boolean emailDuplicated = userRepository.existsByEmail(email);
        if (emailDuplicated) {
            throw new NexterviewException(NexterviewErrorCode.EMAIL_DUPLICATED, email);
        }
    }
}

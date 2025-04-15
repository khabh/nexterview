package com.nexterview.server.service;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.repository.UserRepository;
import com.nexterview.server.security.CustomUserDetails;
import com.nexterview.server.security.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createUser)
                .orElseThrow(() -> new NexterviewException(NexterviewErrorCode.USER_NOT_FOUND));
    }

    private UserDetails createUser(com.nexterview.server.domain.User user) {
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(),
                List.of(new SimpleGrantedAuthority(Role.USER.getRoleName())));
    }
}

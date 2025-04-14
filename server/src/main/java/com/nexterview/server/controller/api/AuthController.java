package com.nexterview.server.controller.api;

import com.nexterview.server.security.TokenProvider;
import com.nexterview.server.service.dto.request.LoginRequest;
import com.nexterview.server.service.dto.response.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public TokenDto authorize(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        String token = tokenProvider.generateToken(authentication);

        return new TokenDto(token);
    }
}

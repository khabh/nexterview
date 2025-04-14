package com.nexterview.server.controller.api;

import com.nexterview.server.security.SecurityConfig;
import com.nexterview.server.security.TokenProvider;
import com.nexterview.server.security.jwt.JwtAccessDeniedHandler;
import com.nexterview.server.security.jwt.JwtAuthenticationEntryPoint;
import com.nexterview.server.security.jwt.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({
        JwtConfig.class,
        TokenProvider.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class,
        SecurityConfig.class,
})
@RequiredArgsConstructor
public class TestSecurityConfig {
}

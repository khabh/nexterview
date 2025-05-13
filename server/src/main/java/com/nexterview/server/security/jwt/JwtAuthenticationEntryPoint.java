package com.nexterview.server.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexterview.server.controller.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException {
        String uri = request.getRequestURI();
        String message = authException.getMessage();
        log.warn("인증되지 않은 접근 시도: URI={}, message={}", uri, message);

        ErrorResponse errorResponse = new ErrorResponse("UNAUTHORIZED", "인증되지 않은 사용자입니다.");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}

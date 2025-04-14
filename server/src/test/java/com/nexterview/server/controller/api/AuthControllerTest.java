package com.nexterview.server.controller.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexterview.server.security.TokenProvider;
import com.nexterview.server.service.dto.request.LoginRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    private String validEmail = "test@example.com";
    private String validPassword = "password123";
    private String validJwt = "potato?";

    @BeforeEach
    void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new User(validEmail, validPassword, List.of()), validPassword);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(validEmail, validPassword)
        )).thenReturn(authentication);

        when(tokenProvider.generateToken(authentication)).thenReturn(validJwt);
    }

    @Test
    void 로그인_토큰을_생성한다() throws Exception {
        LoginRequest loginRequest = new LoginRequest(validEmail, validPassword);

        mockMvc.perform(post("/api/authenticate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(validJwt));
    }

    @Test
    void 로그인_실패시_401을_반환한다() throws Exception {
        String invalidEmail = "invalid@example.com";
        String invalidPassword = "wrongpassword";
        LoginRequest loginRequest = new LoginRequest(invalidEmail, invalidPassword);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(invalidEmail, invalidPassword)
        )).thenThrow(new InternalAuthenticationServiceException("Invalid credentials"));

        mockMvc.perform(post("/api/authenticate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}

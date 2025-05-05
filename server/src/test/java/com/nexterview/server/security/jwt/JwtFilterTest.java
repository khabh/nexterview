package com.nexterview.server.security.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexterview.server.security.CustomUserDetails;
import com.nexterview.server.security.TokenProvider;
import com.nexterview.server.security.jwt.JwtSecurityIntegrationTest.JwtSecurityTestConfig;
import com.nexterview.server.security.jwt.JwtSecurityIntegrationTest.TestController;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        JwtSecurityTestConfig.class,
        TestController.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class,
        TokenProvider.class,
        JwtConfig.class,
        ObjectMapper.class,
})
class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void JWT_있으면_보호된_엔드포인트_접근_가능() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                "test@example.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
        String token = tokenProvider.generateToken(authentication);
        mockMvc.perform(get("/test/secured")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, test@example.com"));
    }

    @Test
    void JwtAuthenticationEntryPoint_JWT_없으면_401_응답() throws Exception {
        mockMvc.perform(get("/test/secured"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"code\":\"UNAUTHORIZED\",\"message\":\"인증되지 않은 사용자입니다.\"}"));
    }

    @Test
    void JwtAccessDeniedHandler_권한_없으면_403_응답() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails(
                1L,
                "test@example.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
        String token = tokenProvider.generateToken(authentication);

        mockMvc.perform(get("/test/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\"code\":\"FORBIDDEN\",\"message\":\"권한이 없는 사용자입니다.\"}"));
    }

    @EnableWebSecurity
    @EnableMethodSecurity
    @TestConfiguration
    public static class JwtSecurityTestConfig {

        @Bean
        public SecurityFilterChain filterChain(
                HttpSecurity http, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                JwtAccessDeniedHandler jwtAccessDeniedHandler, TokenProvider tokenProvider
        ) throws Exception {
            return http
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                            .accessDeniedHandler(jwtAccessDeniedHandler))
                    .addFilterBefore(new JwtFilter(tokenProvider, jwtAuthenticationEntryPoint),
                            UsernamePasswordAuthenticationFilter.class)
                    .build();
        }
    }

    @RestController
    @RequestMapping("/test")
    public static class TestController {

        @GetMapping("/secured")
        public String secured(Authentication authentication) {
            return "Hello, " + authentication.getName();
        }

        @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
        @GetMapping("/admin")
        public String admin() {
            return "Hello, admin";
        }
    }
}

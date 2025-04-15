package com.nexterview.server.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.security.jwt.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class TokenProviderTest {

    private String secretKey = "password secret secret secret secret";
    private JwtConfig jwtConfig;
    private TokenProvider tokenProvider;
    private UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            new CustomUserDetails(1L, "test@example.com", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))),
            null);

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        jwtConfig.setSecret(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        jwtConfig.setTokenExpiration(3600L);
        tokenProvider = new TokenProvider(jwtConfig);
    }

    @Test
    void 토큰을_올바르게_생성한다() {
        String token = tokenProvider.generateToken(authenticationToken);
        assertThat(token).isNotNull();
        assertThat(token).startsWith("eyJ");
    }

    @Test
    void 인증정보를_올바르게_반환한다() {
        String token = tokenProvider.generateToken(authenticationToken);
        Authentication authentication = tokenProvider.getAuthentication(token);
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("test@example.com");
    }

    @Test
    void 토큰이_만료되면_예외가_발생한다() {
        long currentTimeInMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeInMillis - 1000);

        String expiredToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date(currentTimeInMillis))
                .expiration(expirationDate)
                .claim("id", 1)
                .claim("role", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();

        assertThatThrownBy(() -> tokenProvider.getAuthentication(expiredToken))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.JWT_EXPIRED.getMessage());
    }

    @Test
    void 토큰이_유효하지_않으면_예외가_발생한다() {
        long currentTimeInMillis = System.currentTimeMillis();

        String expiredToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date(currentTimeInMillis))
                .expiration(new Date(currentTimeInMillis + 10000))
                .claim("id", 1)
                .claim("role", "ROLE_USER")
                .signWith(Keys.hmacShaKeyFor((secretKey + "invalid").getBytes()))
                .compact();

        assertThatThrownBy(() -> tokenProvider.getAuthentication(expiredToken))
                .isInstanceOf(NexterviewException.class)
                .hasMessageContaining(NexterviewErrorCode.JWT_INVALID.getMessage());
    }

    @Test
    void 유효한_토큰일_경우_검증이_통과_한다() {
        String validToken = tokenProvider.generateToken(authenticationToken);
        assertThatCode(() -> tokenProvider.getAuthentication(validToken))
                .doesNotThrowAnyException();
    }
}

package com.nexterview.server.security;

import com.nexterview.server.exception.NexterviewErrorCode;
import com.nexterview.server.exception.NexterviewException;
import com.nexterview.server.security.jwt.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    private final JwtConfig jwtConfig;
    private final SecretKey key;

    public TokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = generateKey(jwtConfig.getSecret());
    }

    private SecretKey generateKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getTokenExpiration() * 1000);

        return Jwts.builder()
                .subject(principal.getEmail())
                .claim("id", principal.getId())
                .claim("role", principal.getRole())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        Long id = claims.get("id", Integer.class).longValue();
        String role = Role.from(claims.get("role", String.class)).getRoleName();
        CustomUserDetails principal = new CustomUserDetails(id, email, "", List.of(new SimpleGrantedAuthority(role)));

        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException exception) {
            throw new NexterviewException(NexterviewErrorCode.JWT_EXPIRED);
        } catch (Exception exception) {
            throw new NexterviewException(NexterviewErrorCode.JWT_INVALID);
        }
    }
}

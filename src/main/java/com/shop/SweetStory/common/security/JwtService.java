package com.shop.SweetStory.common.security;

import com.shop.SweetStory.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        boolean isAdmin = Boolean.TRUE.equals(user.getIsAdmin());
        String avatar = user.getAvatar() != null ? user.getAvatar() : "";
        String phone = user.getPhone() != null ? user.getPhone() : "";
        String name = user.getName() != null ? user.getName() : "";

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", name)
                .claim("email", user.getEmail())
                .claim("phone", phone)
                .claim("avatar", avatar)
                .claim("is_admin", isAdmin)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAndValidate(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        Object rawId = parseAndValidate(token).get("userId");
        if (rawId instanceof Integer i) {
            return i.longValue();
        }
        if (rawId instanceof Long l) {
            return l;
        }
        return Long.parseLong(String.valueOf(rawId));
    }

    public String extractEmail(String token) {
        return parseAndValidate(token).getSubject();
    }

    public boolean extractIsAdmin(String token) {
        Boolean flag = parseAndValidate(token).get("is_admin", Boolean.class);
        return Boolean.TRUE.equals(flag);
    }
}
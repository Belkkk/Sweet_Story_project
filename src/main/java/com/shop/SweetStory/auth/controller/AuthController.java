package com.shop.SweetStory.auth.controller;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.LoginRequest;
import com.shop.SweetStory.auth.dto.MessageResponse;
import com.shop.SweetStory.auth.dto.RegisterRequest;
import com.shop.SweetStory.auth.service.AuthService;
import com.shop.SweetStory.auth.service.AuthService.AuthSession;
import com.shop.SweetStory.common.security.JwtAuthFilter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final long expirationMs;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(
            AuthService authService,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs,
            @Value("${app.jwt.cookie-secure:false}") boolean cookieSecure,
            @Value("${app.jwt.cookie-same-site:Lax}") String cookieSameSite
    ) {
        this.authService = authService;
        this.expirationMs = expirationMs;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthSession session = authService.register(request);
        ResponseCookie cookie = buildAuthCookie(
                session.token(),
                Duration.ofMillis(expirationMs),
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(session.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthSession session = authService.login(request);
        ResponseCookie cookie = buildAuthCookie(
                session.token(),
                Duration.ofMillis(expirationMs),
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(session.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        ResponseCookie clearedCookie = buildAuthCookie(
                "",
                Duration.ZERO,
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .body(new MessageResponse("Вы успешно вышли из системы"));
    }

    private ResponseCookie buildAuthCookie(
            String value,
            Duration maxAge,
            boolean secure,
            String sameSite
    ) {
        return ResponseCookie.from(JwtAuthFilter.AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(sameSite)
                .maxAge(maxAge)
                .build();
    }
}
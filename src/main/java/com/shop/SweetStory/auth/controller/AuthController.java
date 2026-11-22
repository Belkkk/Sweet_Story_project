package com.shop.SweetStory.auth.controller;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.LoginRequest;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final long expirationMs;

    public AuthController(
            AuthService authService,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.authService = authService;
        this.expirationMs = expirationMs;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthSession session = authService.register(request);
        ResponseCookie cookie = buildAuthCookie(session.token(), Duration.ofMillis(expirationMs));

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(session.response());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthSession session = authService.login(request);
        ResponseCookie cookie = buildAuthCookie(session.token(), Duration.ofMillis(expirationMs));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(session.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie clearedCookie = buildAuthCookie("", Duration.ZERO);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("unauthenticated");
        }

        authService.deleteCurrentAccount(authentication.getName());

        ResponseCookie clearedCookie = buildAuthCookie("", Duration.ZERO);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .build();
    }

    private ResponseCookie buildAuthCookie(String value, Duration maxAge) {
        return ResponseCookie.from(JwtAuthFilter.AUTH_COOKIE_NAME, value)
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAge)
                // Включи .secure(true) при работе по HTTPS (или когда фронт и бэк на разных доменах/портах по HTTPS)
                .build();
    }
}
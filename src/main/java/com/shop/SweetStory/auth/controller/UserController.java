package com.shop.SweetStory.auth.controller;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.MessageResponse;
import com.shop.SweetStory.auth.dto.UpdateProfileRequest;
import com.shop.SweetStory.auth.service.AuthService;
import com.shop.SweetStory.auth.service.AuthService.AuthSession;
import com.shop.SweetStory.common.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final long expirationMs;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public UserController(
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

    @PatchMapping("/me")
    public ResponseEntity<AuthResponse> updateMe(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {
        String email = requireAuthenticatedEmail(authentication);
        AuthSession session = authService.updateProfile(email, request);
        ResponseCookie refreshedCookie = buildAuthCookie(
                session.token(),
                Duration.ofMillis(expirationMs),
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshedCookie.toString())
                .body(session.response());
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponse> deleteMe(Authentication authentication) {
        String email = requireAuthenticatedEmail(authentication);
        authService.deleteCurrentAccount(email);
        ResponseCookie clearedCookie = buildAuthCookie(
                "",
                Duration.ZERO,
                cookieSecure,
                cookieSameSite
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .body(new MessageResponse("Аккаунт успешно удален"));
    }

    private String requireAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("unauthenticated");
        }
        return authentication.getName();
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
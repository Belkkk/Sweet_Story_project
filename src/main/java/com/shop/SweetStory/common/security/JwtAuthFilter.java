package com.shop.SweetStory.common.security;

import com.shop.SweetStory.common.stat.ActiveUserTracker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String AUTH_COOKIE_NAME = "access_token";

    private final JwtService jwtService;
    private final ActiveUserTracker activeUserTracker;

    public JwtAuthFilter(JwtService service, ActiveUserTracker activeUserTracker) {
        this.jwtService = service;
        this.activeUserTracker = activeUserTracker;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractTokenFromCookie(request);

        if (token != null && !token.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtService.extractEmail(token);
                Long userId = jwtService.extractUserId(token);
                boolean isAdmin = jwtService.extractIsAdmin(token);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                if (isAdmin) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                authentication.setDetails(userId);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                activeUserTracker.userSeen(email);
            } catch (Exception ignored) {
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
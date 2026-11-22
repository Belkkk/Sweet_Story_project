package com.shop.SweetStory.auth.service;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.LoginRequest;
import com.shop.SweetStory.auth.dto.RegisterRequest;
import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.common.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public record AuthSession(AuthResponse response, String token) {
    }

    @Transactional
    public AuthSession register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("user with this email already exists");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone().trim());
        user.setIsAdmin(false);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthSession(toAuthResponse(saved), token);
    }

    public AuthSession login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("incorrect email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("incorrect email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthSession(toAuthResponse(user), token);
    }

    @Transactional
    public void deleteCurrentAccount(String email){
        if(email == null || email.isBlank()){
            throw new IllegalStateException("authenticated user email is missing");
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail((normalizedEmail))
                .orElseThrow(()-> new IllegalArgumentException("user not found"));

        userRepository.deleteByEmail(user.getEmail());
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getAvatar(),
                Boolean.TRUE.equals(user.getIsAdmin())
        );
    }
}
package com.shop.SweetStory.auth.service;

import com.shop.SweetStory.auth.dto.AuthResponse;
import com.shop.SweetStory.auth.dto.LoginRequest;
import com.shop.SweetStory.auth.dto.RegisterRequest;
import com.shop.SweetStory.auth.dto.UpdateProfileRequest;
import com.shop.SweetStory.auth.dto.UserProfileDto;
import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.common.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
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

        return new AuthSession(new AuthResponse(toUserProfileDto(saved)), token);
    }

    public AuthSession login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("incorrect email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("incorrect email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthSession(new AuthResponse(toUserProfileDto(user)), token);
    }

    @Transactional
    public AuthSession updateProfile(String email, UpdateProfileRequest request) {
        User user = findByNormalizedEmail(email);

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            user.setPhone(request.phone().trim());
        }
        if (request.avatar() != null) {
            user.setAvatar(request.avatar().isBlank() ? null : request.avatar().trim());
        }

        User saved = userRepository.save(user);
        String refreshedToken = jwtService.generateToken(saved);
        return new AuthSession(new AuthResponse(toUserProfileDto(saved)), refreshedToken);
    }

    @Transactional
    public void deleteCurrentAccount(String email) {
        User user = findByNormalizedEmail(email);
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Cannot delete account while user has associated orders");
        }
    }

    private User findByNormalizedEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("authenticated user email is missing");
        }
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    private UserProfileDto toUserProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                Boolean.TRUE.equals(user.getIsAdmin())
        );
    }
}
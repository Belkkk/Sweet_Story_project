package com.shop.SweetStory.auth.dto;

public record AuthResponse(
        String status,
        UserProfileDto user
) {
    public AuthResponse(UserProfileDto user) {
        this("success", user);
    }
}
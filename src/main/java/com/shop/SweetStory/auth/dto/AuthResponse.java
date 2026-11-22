package com.shop.SweetStory.auth.dto;

public record AuthResponse(
        Long userId,
        String email,
        String name,
        String phone,
        String avatar,
        Boolean isAdmin
) {
}
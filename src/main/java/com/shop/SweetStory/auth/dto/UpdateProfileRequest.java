package com.shop.SweetStory.auth.dto;

public record UpdateProfileRequest(
        String name,
        String phone,
        String avatar
) {
}
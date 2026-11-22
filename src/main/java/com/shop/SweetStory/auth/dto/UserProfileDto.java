package com.shop.SweetStory.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserProfileDto(
        Long id,
        String name,
        String email,
        String phone,
        String avatar,
        @JsonProperty("is_admin")
        Boolean isAdmin
) {
}
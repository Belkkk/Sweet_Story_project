package com.shop.SweetStory.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email
        @NotBlank(message = "login must mot be empty")
        String email,

        @NotBlank(message = "password must not be empty")
        String password
) {
}

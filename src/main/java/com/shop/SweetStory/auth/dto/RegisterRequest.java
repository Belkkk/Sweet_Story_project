package com.shop.SweetStory.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @Email
        @NotBlank(message = "email must not be empty")
        String email,

        @NotBlank(message = "name must not be empty")
        @Size(max = 150, message = "length must be under 150 symbols")
        String name,

        @NotBlank(message = "password must not be empty")
        @Size(max = 100, min = 6, message = "length must be longer then 6 symbols")
        String password,

        @NotBlank(message = "phone must not be empty")
        String phone
) {
}
package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrderCustomerInfo(
        @NotBlank
        String name,

        @NotBlank
        String phone,

        @NotBlank
        @Email
        String email

) {
}

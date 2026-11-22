package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderProductSummary(
        @NotNull
        Long id,

        @NotBlank
        String name,

        String image_url

) {
}

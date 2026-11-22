package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record OrderCreatedItemResponse(
        @NotNull
        Long id,

        @NotNull
        LocalDate delivery_date,

        @NotBlank
        String product_name,

        String filling,

        String decor,

        Double weight,

        String order_status

) {
}

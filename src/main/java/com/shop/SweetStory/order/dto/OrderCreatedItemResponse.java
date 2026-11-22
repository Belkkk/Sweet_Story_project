package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
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

        BigDecimal order_price,

        String order_status

) {
}

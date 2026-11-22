package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MyOrderItemResponse(

        @NotNull
        Long id,

        @NotNull
        LocalDate delivery_date,

        OrderProductSummary product,

        String filling,

        String decor,

        Double weight,

        String order_status

) {
}

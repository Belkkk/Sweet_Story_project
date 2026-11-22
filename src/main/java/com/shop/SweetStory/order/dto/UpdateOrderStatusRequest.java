package com.shop.SweetStory.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotBlank
        String order_status
) {
}

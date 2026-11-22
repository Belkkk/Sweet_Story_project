package com.shop.SweetStory.order.dto;

import java.util.List;

public record AdminOrderResponse(
        List<AdminOrderItemResponse> orders
) {
}

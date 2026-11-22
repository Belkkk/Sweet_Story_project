package com.shop.SweetStory.order.dto;

import java.util.List;

public record MyOrderResponse(
        List<MyOrderItemResponse> orders
) {
}

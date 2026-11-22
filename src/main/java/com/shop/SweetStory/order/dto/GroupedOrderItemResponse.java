package com.shop.SweetStory.order.dto;

import java.math.BigDecimal;

public record GroupedOrderItemResponse(
        Long id,
        String order_status,
        String customer_name,
        String customer_phone,
        String product_name,
        String filling,
        String decor,
        Double weight,
        BigDecimal order_price
) {
}

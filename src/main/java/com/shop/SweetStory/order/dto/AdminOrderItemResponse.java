package com.shop.SweetStory.order.dto;

import java.time.LocalDate;

public record AdminOrderItemResponse(
        Long id,
        LocalDate delivery_date,
        String order_status,
        OrderCustomerInfo customer,
        String product_name,
        String filling,
        String decor,
        Double weight
) {
}

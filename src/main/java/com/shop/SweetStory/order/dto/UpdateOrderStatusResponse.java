package com.shop.SweetStory.order.dto;

public record UpdateOrderStatusResponse(
        String status,
        Long order_id,
        String new_status
) {}

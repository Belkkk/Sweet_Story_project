package com.shop.SweetStory.order.dto;

public record UpdateOrderStatusResponse(
        String status,
        Long order_id,
        String new_status
) {
    public static UpdateOrderStatusResponse success(Long orderId, String newStatus) {
        return new UpdateOrderStatusResponse("success", orderId, newStatus);
    }
}

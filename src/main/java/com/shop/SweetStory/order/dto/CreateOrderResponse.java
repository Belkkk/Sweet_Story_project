package com.shop.SweetStory.order.dto;

public record CreateOrderResponse (
        String status,

        String message,

        OrderCreatedItemResponse order

) {

    public CreateOrderResponse(OrderCreatedItemResponse order){
        this("success", "order successfully created", order);
    }

}

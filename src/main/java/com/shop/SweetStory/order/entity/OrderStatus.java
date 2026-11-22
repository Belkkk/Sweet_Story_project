package com.shop.SweetStory.order.entity;

import java.util.Locale;

public enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public static OrderStatus fromApiValue(String value){
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("Order status must not be empty");
        }
        return OrderStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    public String toApiValue(){
        return name().toLowerCase(Locale.ROOT);
    }

}

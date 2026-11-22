package com.shop.SweetStory.auth.dto;

public record MessageResponse(
        String status,
        String message
) {
    public MessageResponse(String message) {
        this("success", message);
    }
}
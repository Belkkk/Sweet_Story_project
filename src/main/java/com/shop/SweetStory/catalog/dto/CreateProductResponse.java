package com.shop.SweetStory.catalog.dto;

public record CreateProductResponse (
        String status,
        ProductDetailResponse product
) {
}

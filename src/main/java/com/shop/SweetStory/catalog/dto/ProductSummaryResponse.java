package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,
        String name,
        @JsonProperty("image_url") String imageUrl,
        String description,
        @JsonProperty("cake_price") BigDecimal cakePrice
) {
}

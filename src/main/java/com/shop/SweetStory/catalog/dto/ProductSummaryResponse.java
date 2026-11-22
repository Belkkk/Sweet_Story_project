package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductSummaryResponse(
        Long id,
        String name,
        @JsonProperty("image_url") String imageUrl,
        String description
) {
}

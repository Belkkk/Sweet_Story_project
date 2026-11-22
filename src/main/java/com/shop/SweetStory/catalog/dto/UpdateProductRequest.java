package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        @NotBlank(message = "name must not be empty")
        String name,
        List<String> images,
        String description,
        List<String> fillings,
        List<String> decorations,
        @Positive(message = "minWeight must be positive")
        Double minWeight,
        @Positive(message = "maxWeight must be positive")
        Double maxWeight,
        @Positive(message = "cake_price must be positive")
        @JsonProperty("cake_price") BigDecimal cakePrice
) {
}

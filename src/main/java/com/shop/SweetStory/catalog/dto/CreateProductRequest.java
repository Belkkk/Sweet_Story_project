package com.shop.SweetStory.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "name must not be empty")
        String name,
        List<String> images,
        String description,
        @JsonProperty("fillings") List<String> fillings,
        @JsonProperty("decorations") List<String> decorations,
        @NotNull(message = "minWeight is required")
        @Positive(message = "minWeight must be positive")
        Double minWeight,
        @NotNull(message = "maxWeight is required")
        @Positive(message = "maxWeight must be positive")
        Double maxWeight
) {
}

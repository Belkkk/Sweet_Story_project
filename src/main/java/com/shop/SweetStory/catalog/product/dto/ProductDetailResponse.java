package com.shop.SweetStory.catalog.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        List<String> images,
        String description,
        @JsonProperty("available_fillings") List<String> availableFillings,
        @JsonProperty("available_decors") List<String> availableDecors,
        Double minWeight,
        Double maxWeight,
        @JsonProperty("cake_price") BigDecimal cakePrice
) {}

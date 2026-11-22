package com.shop.SweetStory.catalog.dto;

import java.util.List;

public record ProductListResponse (
        List<ProductSummaryResponse> items
){
}

package com.shop.SweetStory.order.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record GroupedOrdersResponse(
        Map<LocalDate, List<GroupedOrderItemResponse>> grouped_orders
) {
}

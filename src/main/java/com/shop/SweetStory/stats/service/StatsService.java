package com.shop.SweetStory.stats.service;

import com.shop.SweetStory.common.stat.ActiveUserTracker;
import com.shop.SweetStory.order.service.OrderService;
import com.shop.SweetStory.stats.dto.OnlineUserResponse;
import com.shop.SweetStory.stats.dto.TotalRevenueResponse;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final ActiveUserTracker activeUserTracker;
    private final OrderService orderService;

    public StatsService(ActiveUserTracker activeUserTracker, OrderService _orderService){
        this.activeUserTracker = activeUserTracker;
        this.orderService = _orderService;
    }

    public TotalRevenueResponse getRevenue() {
        return new TotalRevenueResponse(orderService.getTotalRevenue());
    }

    public OnlineUserResponse getOnlineUsers(){
        int count = (int)activeUserTracker.getOnlineCount();
        return new OnlineUserResponse(count);
    }


}

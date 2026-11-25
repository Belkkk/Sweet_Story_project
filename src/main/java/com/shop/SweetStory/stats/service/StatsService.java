package com.shop.SweetStory.stats.service;

import com.shop.SweetStory.common.stat.ActiveUserTracker;
import com.shop.SweetStory.stats.dto.OnlineUserResponse;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final ActiveUserTracker activeUserTracker;

    public StatsService(ActiveUserTracker tracker){
        this.activeUserTracker = tracker;
    }

    public OnlineUserResponse getOnlineUsers(){
        int count = (int)activeUserTracker.getOnlineCount();
        return new OnlineUserResponse(count);
    }


}

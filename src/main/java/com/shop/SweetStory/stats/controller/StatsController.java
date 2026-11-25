package com.shop.SweetStory.stats.controller;

import com.shop.SweetStory.stats.dto.OnlineUserResponse;
import com.shop.SweetStory.stats.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService service){
        this.statsService = service;
    }

    @GetMapping("/online")
    public ResponseEntity<OnlineUserResponse> getOnlineUsers() {
        return ResponseEntity.ok(statsService.getOnlineUsers());
    }

}

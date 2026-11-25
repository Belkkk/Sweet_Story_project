package com.shop.SweetStory.common.stat;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveUserTracker {

    private final Duration window = Duration.ofMinutes(5);

    private final Map<String, Instant> lastSeen = new ConcurrentHashMap<>();

    public ActiveUserTracker(MeterRegistry registry) {
        Gauge.builder("users.active", this, ActiveUserTracker::getOnlineCount)
                .description("Users active in the last 5 minutes")
                .register(registry);
    }

    public void userSeen(String userId) {
        if (userId != null) {
            lastSeen.put(userId, Instant.now());
        }
    }

    public double getOnlineCount() {
        Instant cutoff = Instant.now().minus(window);

        lastSeen.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));

        return lastSeen.size();
    }
}
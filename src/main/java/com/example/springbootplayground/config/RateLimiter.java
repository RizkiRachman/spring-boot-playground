package com.example.springbootplayground.config;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    private final int requestsPerMinute;
    private final ConcurrentHashMap<String, ClientRequestInfo> clientRequests = new ConcurrentHashMap<>();

    public RateLimiter(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public boolean isAllowed(String clientId) {
        Instant now = Instant.now();
        ClientRequestInfo info = clientRequests.computeIfAbsent(clientId, k -> new ClientRequestInfo());

        synchronized (info) {
            // Reset if minute has passed
            if (now.isAfter(info.windowStart.plusSeconds(60))) {
                info.windowStart = now;
                info.requestCount.set(0);
            }

            if (info.requestCount.get() < requestsPerMinute) {
                info.requestCount.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    private static class ClientRequestInfo {
        Instant windowStart = Instant.now();
        AtomicInteger requestCount = new AtomicInteger(0);
    }
}

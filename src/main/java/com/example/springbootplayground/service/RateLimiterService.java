package com.example.springbootplayground.service;

import com.example.springbootplayground.config.RateLimiterProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, Bucket> endpointBuckets = new ConcurrentHashMap<>();
    private final RateLimiterProperties properties;

    public RateLimiterService(RateLimiterProperties properties) {
        this.properties = properties;
    }

    public boolean isAllowed(String endpointKey, int limitPerMinute) {
        Bucket bucket = endpointBuckets.computeIfAbsent(endpointKey, k -> createBucket(limitPerMinute));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return probe.isConsumed();
    }

    public boolean isAllowed(String endpointKey, String userId, int limitPerMinute) {
        String key = endpointKey + ":" + userId;
        return isAllowed(key, limitPerMinute);
    }

    public boolean isAllowedForEndpoint(String endpoint, String userId) {
        int limit = properties.getLimitForEndpoint(endpoint);

        // Check client-specific override
        int clientLimit = properties.getLimitForClient(userId);
        if (clientLimit > 0) {
            limit = clientLimit;
        }

        return isAllowed(endpoint, userId, limit);
    }

    public void reset(String endpointKey) {
        endpointBuckets.remove(endpointKey);
    }

    public void reset(String endpointKey, String userId) {
        String key = endpointKey + ":" + userId;
        endpointBuckets.remove(key);
    }

    private Bucket createBucket(int limitPerMinute) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(limitPerMinute)
                .refillGreedy(limitPerMinute, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

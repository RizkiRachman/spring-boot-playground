package com.example.springbootplayground.config;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {

    private final int requestsPerMinute;
    private final long windowMillis = 60_000;
    private final ConcurrentHashMap<String, ClientRequestInfo> clientRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor;
    private volatile long lastCleanupTime = System.currentTimeMillis();

    public RateLimiter(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rate-limiter-cleanup");
            t.setDaemon(true);
            return t;
        });
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredEntries, 5, 5, TimeUnit.MINUTES);
    }

    public boolean isAllowed(String clientId) {
        long now = System.currentTimeMillis();

        // Periodic cleanup check (lock-free)
        if (now - lastCleanupTime > 300_000) {
            lastCleanupTime = now;
        }

        ClientRequestInfo info = clientRequests.computeIfAbsent(clientId, k -> new ClientRequestInfo(now));

        // Optimistic locking with compare-and-swap
        long windowStart = info.windowStart.get();
        int currentCount = info.requestCount.get();

        // Reset if window expired
        if (now - windowStart > windowMillis) {
            if (info.windowStart.compareAndSet(windowStart, now)) {
                info.requestCount.set(0);
                currentCount = 0;
            }
        }

        // Try to increment atomically
        if (currentCount < requestsPerMinute) {
            return info.requestCount.compareAndSet(currentCount, currentCount + 1);
        }
        return false;
    }

    private void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, ClientRequestInfo>> it = clientRequests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ClientRequestInfo> entry = it.next();
            ClientRequestInfo info = entry.getValue();
            if (info != null && now - info.windowStart.get() > 600_000) {
                it.remove();
            }
        }
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
    }

    private static class ClientRequestInfo {
        final AtomicLong windowStart;
        final AtomicInteger requestCount;

        ClientRequestInfo(long now) {
            this.windowStart = new AtomicLong(now);
            this.requestCount = new AtomicInteger(0);
        }
    }
}

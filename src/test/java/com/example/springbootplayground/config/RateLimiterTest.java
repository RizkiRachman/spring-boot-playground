package com.example.springbootplayground.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimiter Tests")
class RateLimiterTest {

    @Mock
    private RateLimiterProperties properties;

    @Mock
    private RateLimiterProperties.Filter filterConfig;

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        when(properties.getFilter()).thenReturn(filterConfig);
        when(filterConfig.getRequestsPerMinute()).thenReturn(10);
        rateLimiter = new RateLimiter(properties);
    }

    @Test
    @DisplayName("Should allow request within limit")
    void shouldAllowRequestWithinLimit() {
        // When
        boolean allowed = rateLimiter.isAllowed("client-1");

        // Then
        assertTrue(allowed);
    }

    @Test
    @DisplayName("Should track separate limits per client")
    void shouldTrackSeparateLimitsPerClient() {
        // Given - Use up limit for client-1
        for (int i = 0; i < 10; i++) {
            rateLimiter.isAllowed("client-1");
        }

        // When - Client-1 should be blocked, client-2 should pass
        boolean client1Blocked = rateLimiter.isAllowed("client-1");
        boolean client2Allowed = rateLimiter.isAllowed("client-2");

        // Then
        assertFalse(client1Blocked);
        assertTrue(client2Allowed);
    }

    @Test
    @DisplayName("Should handle null client ID gracefully")
    void shouldHandleNullClientId() {
        // When
        assertThrows(NullPointerException.class, () -> {
            rateLimiter.isAllowed(null);
        });
    }

    @Test
    @DisplayName("Should handle empty client ID")
    void shouldHandleEmptyClientId() {
        // When
        boolean allowed = rateLimiter.isAllowed("");

        // Then
        assertTrue(allowed);
    }

    @Test
    @DisplayName("Should allow exactly 10 requests per minute")
    void shouldAllowExactly10RequestsPerMinute() {
        // Given
        String clientId = "test-client";
        int allowedCount = 0;

        // When - Try 15 requests
        for (int i = 0; i < 15; i++) {
            if (rateLimiter.isAllowed(clientId)) {
                allowedCount++;
            }
        }

        // Then
        assertEquals(10, allowedCount);
    }
}

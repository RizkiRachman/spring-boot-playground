package com.example.springbootplayground.service;

import com.example.springbootplayground.config.RateLimiterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimiterService Tests")
class RateLimiterServiceTest {

    @Mock
    private RateLimiterProperties properties;

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService(properties);
    }

    @Test
    @DisplayName("Should allow request within limit")
    void shouldAllowRequestWithinLimit() {
        // When
        boolean allowed = rateLimiterService.isAllowed("endpoint1", 10);

        // Then
        assertTrue(allowed);
    }

    @Test
    @DisplayName("Should block request when limit exceeded")
    void shouldBlockRequestWhenLimitExceeded() {
        // Given
        String endpoint = "test-endpoint";

        // When - Use up all 10 requests
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isAllowed(endpoint, 10);
        }

        // Then - 11th request should be blocked
        boolean blocked = rateLimiterService.isAllowed(endpoint, 10);
        assertFalse(blocked);
    }

    @Test
    @DisplayName("Should track separate limits per endpoint")
    void shouldTrackSeparateLimitsPerEndpoint() {
        // Given - Use up limit for endpoint1
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isAllowed("endpoint1", 10);
        }

        // When
        boolean endpoint1Blocked = rateLimiterService.isAllowed("endpoint1", 10);
        boolean endpoint2Allowed = rateLimiterService.isAllowed("endpoint2", 10);

        // Then
        assertFalse(endpoint1Blocked);
        assertTrue(endpoint2Allowed);
    }

    @Test
    @DisplayName("Should track separate limits per user")
    void shouldTrackSeparateLimitsPerUser() {
        // Given - Use up limit for user1
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isAllowed("endpoint", "user1", 10);
        }

        // When
        boolean user1Blocked = rateLimiterService.isAllowed("endpoint", "user1", 10);
        boolean user2Allowed = rateLimiterService.isAllowed("endpoint", "user2", 10);

        // Then
        assertFalse(user1Blocked);
        assertTrue(user2Allowed);
    }

    @Test
    @DisplayName("Should reset bucket for endpoint")
    void shouldResetBucketForEndpoint() {
        // Given - Use up limit
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isAllowed("endpoint", 10);
        }
        boolean blockedBefore = rateLimiterService.isAllowed("endpoint", 10);
        assertFalse(blockedBefore);

        // When
        rateLimiterService.reset("endpoint");

        // Then
        boolean allowedAfter = rateLimiterService.isAllowed("endpoint", 10);
        assertTrue(allowedAfter);
    }

    @Test
    @DisplayName("Should reset bucket for specific user")
    void shouldResetBucketForSpecificUser() {
        // Given - Use up limit for specific user
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isAllowed("endpoint", "user1", 10);
        }
        boolean blockedBefore = rateLimiterService.isAllowed("endpoint", "user1", 10);
        assertFalse(blockedBefore);

        // When
        rateLimiterService.reset("endpoint", "user1");

        // Then
        boolean allowedAfter = rateLimiterService.isAllowed("endpoint", "user1", 10);
        assertTrue(allowedAfter);
    }

    @Test
    @DisplayName("Should not fail when resetting non-existent bucket")
    void shouldNotFailWhenResettingNonExistentBucket() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> rateLimiterService.reset("non-existent"));
        assertDoesNotThrow(() -> rateLimiterService.reset("non-existent", "user"));
    }

    @Test
    @DisplayName("Should allow different endpoints with same limit")
    void shouldAllowDifferentEndpointsWithSameLimit() {
        // When
        boolean endpoint1 = rateLimiterService.isAllowed("endpoint1", 10);
        boolean endpoint2 = rateLimiterService.isAllowed("endpoint2", 10);
        boolean endpoint3 = rateLimiterService.isAllowed("endpoint3", 10);

        // Then
        assertTrue(endpoint1);
        assertTrue(endpoint2);
        assertTrue(endpoint3);
    }

    @Test
    @DisplayName("Should handle empty user ID")
    void shouldHandleEmptyUserId() {
        // Given
        when(properties.getLimitForEndpoint("hello")).thenReturn(10);

        // When
        boolean allowed = rateLimiterService.isAllowedForEndpoint("hello", "");

        // Then
        assertTrue(allowed);
    }

    @Test
    @DisplayName("Should use configured limit for endpoint")
    void shouldUseConfiguredLimitForEndpoint() {
        // Given
        when(properties.getLimitForEndpoint("hello")).thenReturn(20);
        when(properties.getLimitForClient(anyString())).thenReturn(-1);

        // When - Use 20 requests
        int allowedCount = 0;
        for (int i = 0; i < 25; i++) {
            if (rateLimiterService.isAllowedForEndpoint("hello", "user1")) {
                allowedCount++;
            }
        }

        // Then
        assertEquals(20, allowedCount);
    }

    @Test
    @DisplayName("Should use client-specific override when available")
    void shouldUseClientSpecificOverrideWhenAvailable() {
        // Given
        when(properties.getLimitForEndpoint("hello")).thenReturn(20);
        when(properties.getLimitForClient("vip-user")).thenReturn(50);

        // When - Use 50 requests
        int allowedCount = 0;
        for (int i = 0; i < 55; i++) {
            if (rateLimiterService.isAllowedForEndpoint("hello", "vip-user")) {
                allowedCount++;
            }
        }

        // Then
        assertEquals(50, allowedCount);
    }

    @Test
    @DisplayName("Should handle negative client limit")
    void shouldHandleNegativeClientLimit() {
        // Given
        when(properties.getLimitForEndpoint("hello")).thenReturn(10);
        when(properties.getLimitForClient("normal-user")).thenReturn(-1);

        // When
        int allowedCount = 0;
        for (int i = 0; i < 15; i++) {
            if (rateLimiterService.isAllowedForEndpoint("hello", "normal-user")) {
                allowedCount++;
            }
        }

        // Then - Should use default limit (10)
        assertEquals(10, allowedCount);
    }

    @Test
    @DisplayName("Should handle zero client limit")
    void shouldHandleZeroClientLimit() {
        // Given
        when(properties.getLimitForEndpoint("hello")).thenReturn(10);
        when(properties.getLimitForClient("blocked-user")).thenReturn(0);

        // When
        int allowedCount = 0;
        for (int i = 0; i < 15; i++) {
            if (rateLimiterService.isAllowedForEndpoint("hello", "blocked-user")) {
                allowedCount++;
            }
        }

        // Then - Should use default limit (10)
        assertEquals(10, allowedCount);
    }
}

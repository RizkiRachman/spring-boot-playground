package com.example.springbootplayground.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RateLimiterProperties Tests")
class RateLimiterPropertiesTest {

    @Test
    @DisplayName("Should create RateLimiterProperties with defaults")
    void shouldCreateRateLimiterPropertiesWithDefaults() {
        // When
        RateLimiterProperties properties = new RateLimiterProperties();

        // Then
        assertNotNull(properties.getFilter());
        assertNotNull(properties.getEndpoints());
        assertTrue(properties.getFilter().isEnabled());
        assertEquals(10, properties.getFilter().getRequestsPerMinute());
    }

    @Test
    @DisplayName("Should get limit for hello endpoint")
    void shouldGetLimitForHelloEndpoint() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limit = properties.getLimitForEndpoint("hello");

        // Then
        assertEquals(20, limit);
    }

    @Test
    @DisplayName("Should get limit for external slow endpoint")
    void shouldGetLimitForExternalSlowEndpoint() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limit = properties.getLimitForEndpoint("external:slow");

        // Then
        assertEquals(5, limit);
    }

    @Test
    @DisplayName("Should get limit for external fast endpoint")
    void shouldGetLimitForExternalFastEndpoint() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limit = properties.getLimitForEndpoint("external:fast");

        // Then
        assertEquals(50, limit);
    }

    @Test
    @DisplayName("Should get limit for premium endpoint")
    void shouldGetLimitForPremiumEndpoint() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limit = properties.getLimitForEndpoint("premium");

        // Then
        assertEquals(100, limit);
    }

    @Test
    @DisplayName("Should get default limit for unknown endpoint")
    void shouldGetDefaultLimitForUnknownEndpoint() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limit = properties.getLimitForEndpoint("unknown");

        // Then
        assertEquals(20, limit);
    }

    @Test
    @DisplayName("Should return -1 for non-existent client")
    void shouldReturnMinusOneForNonExistentClient() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limit = properties.getLimitForClient("192.168.1.1");

        // Then
        assertEquals(-1, limit);
    }

    @Test
    @DisplayName("Should handle case-insensitive endpoint names")
    void shouldHandleCaseInsensitiveEndpointNames() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();

        // When
        int limitUpper = properties.getLimitForEndpoint("HELLO");
        int limitLower = properties.getLimitForEndpoint("hello");
        int limitMixed = properties.getLimitForEndpoint("Hello");

        // Then
        assertEquals(limitLower, limitUpper);
        assertEquals(limitLower, limitMixed);
    }

    @Test
    @DisplayName("Should update filter configuration")
    void shouldUpdateFilterConfiguration() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();
        RateLimiterProperties.Filter filter = new RateLimiterProperties.Filter();
        filter.setEnabled(false);
        filter.setRequestsPerMinute(20);

        // When
        properties.setFilter(filter);

        // Then
        assertFalse(properties.getFilter().isEnabled());
        assertEquals(20, properties.getFilter().getRequestsPerMinute());
    }

    @Test
    @DisplayName("Should update endpoints configuration")
    void shouldUpdateEndpointsConfiguration() {
        // Given
        RateLimiterProperties properties = new RateLimiterProperties();
        RateLimiterProperties.Endpoints endpoints = new RateLimiterProperties.Endpoints();
        endpoints.setHello(100);

        // When
        properties.setEndpoints(endpoints);

        // Then
        assertEquals(100, properties.getLimitForEndpoint("hello"));
    }

    @Test
    @DisplayName("Should update Filter enabled status")
    void shouldUpdateFilterEnabledStatus() {
        // Given
        RateLimiterProperties.Filter filter = new RateLimiterProperties.Filter();
        
        // When
        filter.setEnabled(false);

        // Then
        assertFalse(filter.isEnabled());
    }

    @Test
    @DisplayName("Should update Filter requests per minute")
    void shouldUpdateFilterRequestsPerMinute() {
        // Given
        RateLimiterProperties.Filter filter = new RateLimiterProperties.Filter();
        
        // When
        filter.setRequestsPerMinute(50);

        // Then
        assertEquals(50, filter.getRequestsPerMinute());
    }

    @Test
    @DisplayName("Should update all endpoint values")
    void shouldUpdateAllEndpointValues() {
        // Given
        RateLimiterProperties.Endpoints endpoints = new RateLimiterProperties.Endpoints();
        
        // When
        endpoints.setHello(30);
        endpoints.setExternalDefault(40);
        endpoints.setExternalSlow(10);
        endpoints.setExternalFast(100);
        endpoints.setPremium(200);

        // Then
        assertEquals(30, endpoints.getHello());
        assertEquals(40, endpoints.getExternalDefault());
        assertEquals(10, endpoints.getExternalSlow());
        assertEquals(100, endpoints.getExternalFast());
        assertEquals(200, endpoints.getPremium());
    }
}

package com.example.springbootplayground.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RequestUtils Tests")
class RequestUtilsTest {

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("Should extract client IP from X-Forwarded-For header")
    void shouldExtractClientIpFromXForwardedFor() {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.100, 10.0.0.1");

        // When
        String clientId = RequestUtils.getClientId(request);

        // Then
        assertEquals("192.168.1.100", clientId);
    }

    @Test
    @DisplayName("Should extract single IP from X-Forwarded-For header")
    void shouldExtractSingleIpFromXForwardedFor() {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.100");

        // When
        String clientId = RequestUtils.getClientId(request);

        // Then
        assertEquals("192.168.1.100", clientId);
    }

    @Test
    @DisplayName("Should fall back to RemoteAddr when X-Forwarded-For is null")
    void shouldFallbackToRemoteAddrWhenXForwardedForNull() {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("10.0.0.50");

        // When
        String clientId = RequestUtils.getClientId(request);

        // Then
        assertEquals("10.0.0.50", clientId);
    }

    @Test
    @DisplayName("Should return unknown when both headers are null")
    void shouldReturnUnknownWhenBothHeadersNull() {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(null);

        // When
        String clientId = RequestUtils.getClientId(request);

        // Then
        assertEquals("unknown", clientId);
    }

    @Test
    @DisplayName("Should handle empty X-Forwarded-For")
    void shouldHandleEmptyXForwardedFor() {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // When
        String clientId = RequestUtils.getClientId(request);

        // Then
        assertEquals("127.0.0.1", clientId);
    }

    @Test
    @DisplayName("Should handle whitespace in X-Forwarded-For")
    void shouldHandleWhitespaceInXForwardedFor() {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("  192.168.1.100  ,  10.0.0.1  ");

        // When
        String clientId = RequestUtils.getClientId(request);

        // Then
        assertEquals("192.168.1.100", clientId);
    }
}

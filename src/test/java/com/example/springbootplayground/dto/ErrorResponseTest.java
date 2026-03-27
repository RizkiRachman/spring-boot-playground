package com.example.springbootplayground.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse DTO Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create ErrorResponse using builder")
    void shouldCreateErrorResponseUsingBuilder() {
        // Given
        String id = "test-id-123";
        int errorCode = 429;
        String errorMessage = "Too Many Requests";
        String detailMessage = "Rate limit exceeded";
        String timestamp = "2026-03-27T10:00:00Z";

        // When
        ErrorResponse response = ErrorResponse.builder()
                .id(id)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .detailMessage(detailMessage)
                .timestamp(timestamp)
                .build();

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(errorMessage, response.getErrorMessage());
        assertEquals(detailMessage, response.getDetailMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    @DisplayName("Should create ErrorResponse using no-args constructor")
    void shouldCreateErrorResponseUsingNoArgsConstructor() {
        // When
        ErrorResponse response = new ErrorResponse();

        // Then
        assertNotNull(response);
        assertNull(response.getId());
        assertEquals(0, response.getErrorCode());
        assertNull(response.getErrorMessage());
        assertNull(response.getDetailMessage());
        assertNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create ErrorResponse using all-args constructor")
    void shouldCreateErrorResponseUsingAllArgsConstructor() {
        // Given
        String id = "test-id-456";
        int errorCode = 500;
        String errorMessage = "Internal Server Error";
        String detailMessage = "Something went wrong";
        String timestamp = "2026-03-27T11:00:00Z";

        // When
        ErrorResponse response = new ErrorResponse(id, errorCode, errorMessage, detailMessage, timestamp);

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(errorMessage, response.getErrorMessage());
        assertEquals(detailMessage, response.getDetailMessage());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    @DisplayName("Should update fields using setters")
    void shouldUpdateFieldsUsingSetters() {
        // Given
        ErrorResponse response = new ErrorResponse();

        // When
        response.setId("updated-id");
        response.setErrorCode(400);
        response.setErrorMessage("Bad Request");
        response.setDetailMessage("Invalid input");
        response.setTimestamp("2026-03-27T12:00:00Z");

        // Then
        assertEquals("updated-id", response.getId());
        assertEquals(400, response.getErrorCode());
        assertEquals("Bad Request", response.getErrorMessage());
        assertEquals("Invalid input", response.getDetailMessage());
        assertEquals("2026-03-27T12:00:00Z", response.getTimestamp());
    }

    @Test
    @DisplayName("Should handle null values in builder")
    void shouldHandleNullValuesInBuilder() {
        // When
        ErrorResponse response = ErrorResponse.builder()
                .id(null)
                .errorCode(0)
                .errorMessage(null)
                .detailMessage(null)
                .timestamp(null)
                .build();

        // Then
        assertNotNull(response);
        assertNull(response.getId());
        assertEquals(0, response.getErrorCode());
        assertNull(response.getErrorMessage());
        assertNull(response.getDetailMessage());
        assertNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create multiple independent instances")
    void shouldCreateMultipleIndependentInstances() {
        // When
        ErrorResponse response1 = ErrorResponse.builder()
                .id("id-1")
                .errorCode(429)
                .errorMessage("Error 1")
                .detailMessage("Detail 1")
                .timestamp("2026-03-27T10:00:00Z")
                .build();

        ErrorResponse response2 = ErrorResponse.builder()
                .id("id-2")
                .errorCode(500)
                .errorMessage("Error 2")
                .detailMessage("Detail 2")
                .timestamp("2026-03-27T11:00:00Z")
                .build();

        // Then
        assertNotEquals(response1.getId(), response2.getId());
        assertNotEquals(response1.getErrorCode(), response2.getErrorCode());
        assertNotEquals(response1.getErrorMessage(), response2.getErrorMessage());
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        // Given
        ErrorResponse response1 = ErrorResponse.builder()
                .id("same-id")
                .errorCode(429)
                .errorMessage("Same Error")
                .detailMessage("Same Detail")
                .timestamp("2026-03-27T10:00:00Z")
                .build();

        ErrorResponse response2 = ErrorResponse.builder()
                .id("same-id")
                .errorCode(429)
                .errorMessage("Same Error")
                .detailMessage("Same Detail")
                .timestamp("2026-03-27T10:00:00Z")
                .build();

        // Then
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should support toString")
    void shouldSupportToString() {
        // Given
        ErrorResponse response = ErrorResponse.builder()
                .id("test-id")
                .errorCode(429)
                .errorMessage("Test Error")
                .detailMessage("Test Detail")
                .timestamp("2026-03-27T10:00:00Z")
                .build();

        // When
        String result = response.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("test-id"));
        assertTrue(result.contains("429"));
        assertTrue(result.contains("Test Error"));
    }
}

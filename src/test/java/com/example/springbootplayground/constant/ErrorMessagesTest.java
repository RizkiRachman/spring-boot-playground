package com.example.springbootplayground.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorMessages Constants Tests")
class ErrorMessagesTest {

    @Test
    @DisplayName("Should have correct rate limit exceeded message")
    void shouldHaveCorrectRateLimitExceededMessage() {
        assertEquals("Rate limit exceeded. Try again later.", 
                ErrorMessages.RATE_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("Should have correct internal server error message")
    void shouldHaveCorrectInternalServerErrorMessage() {
        assertEquals("Internal Server Error", 
                ErrorMessages.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should have correct too many requests message")
    void shouldHaveCorrectTooManyRequestsMessage() {
        assertEquals("Too Many Requests", 
                ErrorMessages.TOO_MANY_REQUESTS);
    }

    @Test
    @DisplayName("Should have non-null constants")
    void shouldHaveNonNullConstants() {
        assertNotNull(ErrorMessages.RATE_LIMIT_EXCEEDED);
        assertNotNull(ErrorMessages.INTERNAL_SERVER_ERROR);
        assertNotNull(ErrorMessages.TOO_MANY_REQUESTS);
    }

    @Test
    @DisplayName("Should have non-empty constants")
    void shouldHaveNonEmptyConstants() {
        assertFalse(ErrorMessages.RATE_LIMIT_EXCEEDED.isEmpty());
        assertFalse(ErrorMessages.INTERNAL_SERVER_ERROR.isEmpty());
        assertFalse(ErrorMessages.TOO_MANY_REQUESTS.isEmpty());
    }
}

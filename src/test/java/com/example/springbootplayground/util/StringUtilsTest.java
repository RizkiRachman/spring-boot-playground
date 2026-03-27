package com.example.springbootplayground.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils Tests")
class StringUtilsTest {

    @Test
    @DisplayName("Should generate ID with correct length")
    void shouldGenerateIdWithCorrectLength() {
        // When
        String id = StringUtils.generateFastId();

        // Then
        assertEquals(16, id.length());
    }

    @Test
    @DisplayName("Should generate unique IDs")
    void shouldGenerateUniqueIds() {
        // Given
        Set<String> ids = new HashSet<>();

        // When
        for (int i = 0; i < 1000; i++) {
            ids.add(StringUtils.generateFastId());
        }

        // Then
        assertEquals(1000, ids.size(), "All IDs should be unique");
    }

    @RepeatedTest(100)
    @DisplayName("Should generate ID with valid characters")
    void shouldGenerateIdWithValidCharacters() {
        // When
        String id = StringUtils.generateFastId();

        // Then
        assertTrue(id.matches("^[A-Za-z0-9]+$"), "ID should contain only alphanumeric characters");
    }

    @Test
    @DisplayName("Should not generate null ID")
    void shouldNotGenerateNullId() {
        // When
        String id = StringUtils.generateFastId();

        // Then
        assertNotNull(id);
    }

    @Test
    @DisplayName("Should not generate empty ID")
    void shouldNotGenerateEmptyId() {
        // When
        String id = StringUtils.generateFastId();

        // Then
        assertFalse(id.isEmpty());
    }

    @Test
    @DisplayName("Should generate IDs with consistent length")
    void shouldGenerateIdsWithConsistentLength() {
        // When & Then
        for (int i = 0; i < 100; i++) {
            String id = StringUtils.generateFastId();
            assertEquals(16, id.length(), "All IDs should have consistent length");
        }
    }

}

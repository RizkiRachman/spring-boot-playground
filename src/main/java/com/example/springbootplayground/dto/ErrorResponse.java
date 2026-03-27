package com.example.springbootplayground.dto;

public record ErrorResponse(
        String id,
        int errorCode,
        String errorMessage,
        String detailMessage,
        String timestamp
) {
}

package com.example.springbootplayground.exception;

import com.example.springbootplayground.constant.ErrorMessages;
import com.example.springbootplayground.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID().toString(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ErrorMessages.TOO_MANY_REQUESTS,
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                UUID.randomUUID().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorMessages.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                LocalDateTime.now().toString()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

package com.example.springbootplayground.exception;

import com.dev.common.string.StringUtils;
import com.example.springbootplayground.constant.ErrorMessages;
import com.example.springbootplayground.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        String errorId = StringUtils.generateFastId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .id(errorId)
                .errorCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .errorMessage(ErrorMessages.TOO_MANY_REQUESTS)
                .detailMessage(ex.getMessage())
                .timestamp(Instant.now().toString())
                .build();

        log.debug("Rate limit exceeded: errorId={}", errorId);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        String errorId = StringUtils.generateFastId();

        log.error("Unhandled exception: errorId={}", errorId, ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .id(errorId)
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ErrorMessages.INTERNAL_SERVER_ERROR)
                .detailMessage("An unexpected error occurred. Reference: " + errorId)
                .timestamp(Instant.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

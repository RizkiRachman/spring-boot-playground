package com.example.springbootplayground.exception;

import com.example.springbootplayground.constant.ErrorMessages;
import com.example.springbootplayground.dto.ErrorResponse;
import com.example.springbootplayground.util.StringUtils;
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

        ErrorResponse errorResponse = new ErrorResponse(
                errorId,
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ErrorMessages.TOO_MANY_REQUESTS,
                ex.getMessage(),
                Instant.now().toString()
        );

        log.debug("Rate limit exceeded: errorId={}", errorId);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        String errorId = StringUtils.generateFastId();

        log.error("Unhandled exception: errorId={}", errorId, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                errorId,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorMessages.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Reference: " + errorId,
                Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

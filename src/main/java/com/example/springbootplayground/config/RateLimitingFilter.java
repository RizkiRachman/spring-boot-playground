package com.example.springbootplayground.config;

import com.example.springbootplayground.constant.ErrorMessages;
import com.example.springbootplayground.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String clientId = getClientId(request);

        if (!rateLimiter.isAllowed(clientId)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .errorCode(HttpStatus.TOO_MANY_REQUESTS.value())
                    .errorMessage(ErrorMessages.TOO_MANY_REQUESTS)
                    .detailMessage(ErrorMessages.RATE_LIMIT_EXCEEDED)
                    .timestamp(Instant.now().toString())
                    .build();

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(jsonResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientId(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            int commaIndex = xForwardedFor.indexOf(',');
            if (commaIndex > 0) {
                return xForwardedFor.substring(0, commaIndex).trim();
            }
            return xForwardedFor.trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "unknown";
    }
}

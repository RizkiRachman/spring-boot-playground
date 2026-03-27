package com.example.springbootplayground.config;

import com.example.springbootplayground.constant.ErrorMessages;
import com.example.springbootplayground.dto.ErrorResponse;
import com.example.springbootplayground.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientId = getClientId(request);

        if (!rateLimiter.isAllowed(clientId)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    StringUtils.generateFastId(),
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    ErrorMessages.TOO_MANY_REQUESTS,
                    ErrorMessages.RATE_LIMIT_EXCEEDED,
                    Instant.now().toString()
            );

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
            // Manual parse to avoid regex split overhead
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

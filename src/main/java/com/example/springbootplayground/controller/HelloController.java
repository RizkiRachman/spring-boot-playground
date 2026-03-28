package com.example.springbootplayground.controller;

import com.dev.common.exception.RateLimitExceededException;
import com.dev.common.string.StringUtils;
import com.example.springbootplayground.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import static com.example.springbootplayground.util.RequestUtils.getClientId;

@RestController
public class HelloController {

    private final RateLimiterService rateLimiterService;

    public HelloController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, World!");
    }

    @GetMapping("/api/external")
    public ResponseEntity<?> callExternalApi(
            @RequestParam(defaultValue = "default") String apiName,
            HttpServletRequest request) {

        String clientId = getClientId(request);
        String endpointKey = "external:" + apiName;

        // Service-layer rate limiting with properties configuration
        if (!rateLimiterService.isAllowedForEndpoint(endpointKey, clientId)) {
            throw new RateLimitExceededException(
                    "Rate limit exceeded for API: " + apiName,
                    endpointKey,
                    60
            );
        }

        // Simulate external API call
        return ResponseEntity.ok(Map.of(
                "message", "External API call successful",
                "api", apiName,
                "clientId", clientId,
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/api/premium")
    public ResponseEntity<?> premiumApi(HttpServletRequest request) {
        String clientId = getClientId(request);

        // Service-layer rate limiting with properties configuration
        if (!rateLimiterService.isAllowedForEndpoint("premium", clientId)) {
            throw new RateLimitExceededException(
                    "Premium API rate limit exceeded",
                    "premium",
                    60
            );
        }

        return ResponseEntity.ok(Map.of(
                "message", "Premium API call successful",
                "tier", "premium",
                "clientId", clientId,
                "timestamp", Instant.now().toString()
        ));
    }
}
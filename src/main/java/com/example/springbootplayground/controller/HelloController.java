package com.example.springbootplayground.controller;

import com.example.springbootplayground.service.RateLimiterService;
import com.example.springbootplayground.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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
        if (rateLimiterService.isAllowedForEndpoint(endpointKey, clientId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "id", StringUtils.generateFastId(),
                            "errorCode", 429,
                            "errorMessage", "Too Many Requests",
                            "detailMessage", "Rate limit exceeded for API: " + apiName,
                            "timestamp", Instant.now().toString()
                    ));
        }

        // Simulate external API call
        return ResponseEntity.ok(Map.of(
                "message", "External API call successful",
                "api", apiName,
                "clientId", clientId
        ));
    }

    @GetMapping("/api/premium")
    public ResponseEntity<?> premiumApi(HttpServletRequest request) {
        String clientId = getClientId(request);

        // Service-layer rate limiting with properties configuration
        if (rateLimiterService.isAllowedForEndpoint("premium", clientId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "id", StringUtils.generateFastId(),
                            "errorCode", 429,
                            "errorMessage", "Too Many Requests",
                            "detailMessage", "Premium API rate limit exceeded",
                            "timestamp", Instant.now().toString()
                    ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Premium API call successful",
                "tier", "premium",
                "clientId", clientId
        ));
    }
}

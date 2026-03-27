package com.example.springbootplayground.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Setter
@Getter
@ConfigurationProperties(prefix = "rate.limiter")
public class RateLimiterProperties {

    private Filter filter = new Filter();
    private Endpoints endpoints = new Endpoints();
    private Map<String, Integer> clients;

    @Setter
    @Getter
    public static class Filter {
        private boolean enabled = true;
        private int requestsPerMinute = 10;

    }

    @Setter
    @Getter
    public static class Endpoints {
        private int hello = 20;
        private int externalDefault = 20;
        private int externalSlow = 5;
        private int externalFast = 50;
        private int premium = 100;

    }

    public int getLimitForEndpoint(String endpoint) {
        return switch (endpoint.toLowerCase()) {
            case "hello" -> endpoints.getHello();
            case "external:slow" -> endpoints.getExternalSlow();
            case "external:fast" -> endpoints.getExternalFast();
            case "premium" -> endpoints.getPremium();
            default -> endpoints.getExternalDefault();
        };
    }

    public int getLimitForClient(String clientId) {
        if (clients != null && clients.containsKey(clientId)) {
            return clients.get(clientId);
        }
        return -1; // Return -1 to indicate no override
    }
}

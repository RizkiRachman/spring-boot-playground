package com.example.springbootplayground.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "rate.limiter")
public class RateLimiterProperties {

    private Filter filter = new Filter();
    private Endpoints endpoints = new Endpoints();
    private Map<String, Integer> clients;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public Map<String, Integer> getClients() {
        return clients;
    }

    public void setClients(Map<String, Integer> clients) {
        this.clients = clients;
    }

    public static class Filter {
        private boolean enabled = true;
        private int requestsPerMinute = 10;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }

    public static class Endpoints {
        private int hello = 20;
        private int externalDefault = 20;
        private int externalSlow = 5;
        private int externalFast = 50;
        private int premium = 100;

        public int getHello() {
            return hello;
        }

        public void setHello(int hello) {
            this.hello = hello;
        }

        public int getExternalDefault() {
            return externalDefault;
        }

        public void setExternalDefault(int externalDefault) {
            this.externalDefault = externalDefault;
        }

        public int getExternalSlow() {
            return externalSlow;
        }

        public void setExternalSlow(int externalSlow) {
            this.externalSlow = externalSlow;
        }

        public int getExternalFast() {
            return externalFast;
        }

        public void setExternalFast(int externalFast) {
            this.externalFast = externalFast;
        }

        public int getPremium() {
            return premium;
        }

        public void setPremium(int premium) {
            this.premium = premium;
        }
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
        return -1;
    }
}

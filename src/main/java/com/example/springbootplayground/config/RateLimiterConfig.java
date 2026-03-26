package com.example.springbootplayground.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Value("${rate.limiter.requests.per.minute:10}")
    private int requestsPerMinute;

    @Bean
    public RateLimiter rateLimiter() {
        return new RateLimiter(requestsPerMinute);
    }
}

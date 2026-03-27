package com.example.springbootplayground.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfig {

    @Bean
    public RateLimiter rateLimiter(RateLimiterProperties properties) {
        return new RateLimiter(properties);
    }
}

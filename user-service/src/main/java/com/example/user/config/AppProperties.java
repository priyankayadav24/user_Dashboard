package com.example.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, String internalApiKey) {

    public record Jwt(String secret) {}
}

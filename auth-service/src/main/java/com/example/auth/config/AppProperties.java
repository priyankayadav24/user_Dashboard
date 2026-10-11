package com.example.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, UserService userService) {

    public record Jwt(String secret, long expirationMs) {}

    public record UserService(String baseUrl, String internalApiKey) {}
}

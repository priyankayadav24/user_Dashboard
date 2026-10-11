package com.example.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String firstName,
        String lastName,
        String userType,
        String message) {
}

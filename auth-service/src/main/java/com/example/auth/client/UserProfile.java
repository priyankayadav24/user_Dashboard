package com.example.auth.client;

public record UserProfile(
        Long id,
        String firstName,
        String lastName,
        String email,
        String mobileNumber,
        String userType) {
}

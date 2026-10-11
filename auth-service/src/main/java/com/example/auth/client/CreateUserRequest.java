package com.example.auth.client;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        String mobileNumber) {
}

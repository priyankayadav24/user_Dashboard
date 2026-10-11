package com.example.user.dto;

import com.example.user.entity.UserInfo;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String mobileNumber,
        String userType) {

    public static UserResponse from(UserInfo user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getUserType().name());
    }
}

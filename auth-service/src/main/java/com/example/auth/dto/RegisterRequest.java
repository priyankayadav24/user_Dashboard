package com.example.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "mobileNumber must be 8-15 digits") String mobileNumber,
        @NotBlank @Size(min = 8, max = 72) String password) {
}

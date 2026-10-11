package com.example.auth.service;

import com.example.auth.client.CreateUserRequest;
import com.example.auth.client.UserProfile;
import com.example.auth.client.UserServiceClient;
import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.MessageResponse;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.UserCredential;
import com.example.auth.exception.AppException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;

    /**
     * Writes the credential locally and the profile remotely. If the remote call fails,
     * the thrown exception rolls back the local insert, so both sides stay consistent.
     */
    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String email = normalize(request.email());

        if (credentialRepository.existsByEmail(email)) {
            throw AppException.conflict("User already exists - Please login");
        }

        credentialRepository.save(UserCredential.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .build());

        userServiceClient.createUser(new CreateUserRequest(
                request.firstName().trim(),
                request.lastName().trim(),
                email,
                request.mobileNumber().trim()));

        return new MessageResponse("Registration successful");
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());

        UserCredential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> AppException.notFound("User not exist - Please register"));

        if (!passwordEncoder.matches(request.password(), credential.getPassword())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        UserProfile profile = userServiceClient.getProfile(email);
        String token = jwtService.generateToken(email, profile.userType());
        String role = "A".equals(profile.userType()) ? "Admin" : "User";

        return new AuthResponse(
                token,
                "Bearer",
                profile.firstName(),
                profile.lastName(),
                profile.userType(),
                "Successful Login as Userrole as " + role);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

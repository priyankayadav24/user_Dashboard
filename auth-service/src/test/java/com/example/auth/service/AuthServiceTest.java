package com.example.auth.service;

import com.example.auth.client.UserServiceClient;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.UserCredential;
import com.example.auth.exception.AppException;
import com.example.auth.repository.UserCredentialRepository;
import com.example.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserCredentialRepository repository;
    @Mock UserServiceClient userServiceClient;
    @Mock JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(repository, encoder, userServiceClient, jwtService);
    }

    @Test
    void login_unknownEmail_returnsNotFound() {
        when(repository.findByEmail("new@x.com")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> authService.login(new LoginRequest("new@x.com", "secret123")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("User not exist - Please register", ex.getMessage());
    }

    @Test
    void login_wrongPassword_returnsUnauthorized() {
        UserCredential credential = UserCredential.builder()
                .email("a@x.com")
                .password(encoder.encode("right-pass"))
                .build();
        when(repository.findByEmail("a@x.com")).thenReturn(Optional.of(credential));

        AppException ex = assertThrows(AppException.class,
                () -> authService.login(new LoginRequest("a@x.com", "wrong-pass")));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        verifyNoInteractions(userServiceClient, jwtService);
    }

    @Test
    void register_existingEmail_returnsConflictAndDoesNotSave() {
        when(repository.existsByEmail("a@x.com")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> authService.register(
                new RegisterRequest("A", "B", "a@x.com", "9876543210", "secret123")));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(repository, never()).save(any());
    }
}

package com.example.user.controller;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.UserResponse;
import com.example.user.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Called only by auth-service. Protected by InternalApiKeyFilter. */
@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserInfoService userInfoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userInfoService.createUser(request);
    }

    @GetMapping("/{email}")
    public UserResponse getByEmail(@PathVariable String email) {
        return userInfoService.getByEmail(email);
    }
}

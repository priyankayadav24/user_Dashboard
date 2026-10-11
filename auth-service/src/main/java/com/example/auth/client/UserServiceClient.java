package com.example.auth.client;

import com.example.auth.config.AppProperties;
import com.example.auth.exception.AppException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Typed client for user-service. Translates HTTP failures into domain-level AppExceptions
 * so the rest of auth-service never deals with transport details.
 */
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder builder, AppProperties properties) {
        this.restClient = builder
                .baseUrl(properties.userService().baseUrl())
                .defaultHeader("X-Internal-Api-Key", properties.userService().internalApiKey())
                .build();
    }

    public UserProfile createUser(CreateUserRequest request) {
        try {
            return restClient.post()
                    .uri("/api/internal/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(UserProfile.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 409) {
                throw AppException.conflict("User already registered");
            }
            throw AppException.serviceUnavailable("User service rejected the request");
        } catch (RestClientException ex) {
            throw AppException.serviceUnavailable("User service is unavailable, please try again later");
        }
    }

    public UserProfile getProfile(String email) {
        try {
            return restClient.get()
                    .uri("/api/internal/users/{email}", email)
                    .retrieve()
                    .body(UserProfile.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw AppException.notFound("User profile not found");
            }
            throw AppException.serviceUnavailable("User service rejected the request");
        } catch (RestClientException ex) {
            throw AppException.serviceUnavailable("User service is unavailable, please try again later");
        }
    }
}

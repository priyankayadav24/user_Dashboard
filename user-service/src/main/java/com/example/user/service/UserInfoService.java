package com.example.user.service;

import com.example.user.dto.CreateUserRequest;
import com.example.user.dto.UserResponse;
import com.example.user.entity.UserInfo;
import com.example.user.entity.UserType;
import com.example.user.exception.AppException;
import com.example.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private static final String CSV_HEADER = "FirstName,LastName,Email,MobileNumber";

    private final UserInfoRepository repository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = normalize(request.email());

        if (repository.existsByEmail(email)) {
            throw AppException.conflict("User already exists");
        }

        UserInfo saved = repository.save(UserInfo.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .mobileNumber(request.mobileNumber().trim())
                .email(email)
                .userType(UserType.U)
                .build());

        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return repository.findByEmail(normalize(email))
                .map(UserResponse::from)
                .orElseThrow(() -> AppException.notFound("User not found"));
    }

    /** Admins get every user; regular users get only their own record. */
    @Transactional(readOnly = true)
    public List<UserResponse> getVisibleUsers(String email, boolean admin) {
        if (admin) {
            return repository.findAll(Sort.by("id")).stream().map(UserResponse::from).toList();
        }
        return List.of(getByEmail(email));
    }

    @Transactional(readOnly = true)
    public String exportUsersAsCsv() {
        StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
        repository.findAll(Sort.by("id")).forEach(u -> csv
                .append(quote(u.getFirstName())).append(',')
                .append(quote(u.getLastName())).append(',')
                .append(quote(u.getEmail())).append(',')
                .append(quote(u.getMobileNumber())).append('\n'));
        return csv.toString();
    }

    private String quote(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

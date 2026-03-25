package com.example.api.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        boolean active,
        LocalDateTime createdAt
) {
}

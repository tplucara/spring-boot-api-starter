package com.example.api.user.mapper;

import com.example.api.user.dto.UserResponse;
import com.example.api.user.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}

package com.example.api.user.usecase;

import com.example.api.shared.exception.ResourceNotFoundException;
import com.example.api.user.dto.UserResponse;
import com.example.api.user.mapper.UserMapper;
import com.example.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserUseCase {

    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse execute(UUID userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}

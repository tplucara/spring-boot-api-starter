package com.example.api.user.usecase;

import com.example.api.shared.exception.BusinessException;
import com.example.api.user.dto.CreateUserRequest;
import com.example.api.user.dto.UserResponse;
import com.example.api.user.entity.User;
import com.example.api.user.mapper.UserMapper;
import com.example.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User {} created with email {}", savedUser.getId(), savedUser.getEmail());

        return UserMapper.toResponse(savedUser);
    }
}

package com.example.api.user.usecase;

import com.example.api.shared.exception.ResourceNotFoundException;
import com.example.api.user.dto.UserResponse;
import com.example.api.user.entity.User;
import com.example.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @Mock private UserRepository userRepository;

    private GetUserUseCase getUserUseCase;

    @BeforeEach
    void setUp() {
        getUserUseCase = new GetUserUseCase(userRepository);
    }

    @Test
    void execute_shouldReturnUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse result = getUserUseCase.execute(userId);

        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.active()).isTrue();
    }

    @Test
    void execute_shouldThrowResourceNotFound_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getUserUseCase.execute(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}

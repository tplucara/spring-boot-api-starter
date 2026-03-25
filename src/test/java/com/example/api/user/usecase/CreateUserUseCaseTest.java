package com.example.api.user.usecase;

import com.example.api.shared.exception.BusinessException;
import com.example.api.user.dto.CreateUserRequest;
import com.example.api.user.dto.UserResponse;
import com.example.api.user.entity.User;
import com.example.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCase(userRepository, passwordEncoder);
    }

    @Test
    void execute_shouldCreateUser_whenEmailIsNew() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-hash");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail("new@example.com");
        savedUser.setPasswordHash("encoded-hash");
        savedUser.setActive(true);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = createUserUseCase.execute(new CreateUserRequest("new@example.com", "password123"));

        assertThat(result.email()).isEqualTo("new@example.com");
        assertThat(result.active()).isTrue();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-hash");
    }

    @Test
    void execute_shouldThrowBusinessException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> createUserUseCase.execute(new CreateUserRequest("existing@example.com", "password123")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
    }
}

package com.example.api.auth.usecase;

import com.example.api.auth.dto.LoginRequest;
import com.example.api.auth.dto.TokenResponse;
import com.example.api.auth.repository.RefreshTokenRepository;
import com.example.api.shared.config.JwtProperties;
import com.example.api.shared.exception.UnauthorizedException;
import com.example.api.shared.security.JwtTokenProvider;
import com.example.api.user.entity.User;
import com.example.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private JwtProperties jwtProperties;
    @Mock private PasswordEncoder passwordEncoder;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(
                userRepository, refreshTokenRepository, jwtTokenProvider, jwtProperties, passwordEncoder);
    }

    @Test
    void execute_shouldReturnTokens_whenCredentialsAreValid() {
        User user = createUser();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken()).thenReturn("refresh-token");
        when(jwtTokenProvider.hashToken("refresh-token")).thenReturn("hashed-refresh");
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800000L);

        TokenResponse result = loginUseCase.execute(new LoginRequest("user@example.com", "password123"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void execute_shouldThrowUnauthorized_whenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.execute(new LoginRequest("unknown@example.com", "password")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void execute_shouldThrowUnauthorized_whenPasswordIsWrong() {
        User user = createUser();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.execute(new LoginRequest("user@example.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void execute_shouldThrowUnauthorized_whenAccountIsDeactivated() {
        User user = createUser();
        user.setActive(false);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> loginUseCase.execute(new LoginRequest("user@example.com", "password123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Account is deactivated");
    }

    private User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPasswordHash("hashed");
        user.setActive(true);
        return user;
    }
}

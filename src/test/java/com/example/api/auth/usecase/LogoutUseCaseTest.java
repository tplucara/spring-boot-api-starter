package com.example.api.auth.usecase;

import com.example.api.auth.dto.RefreshRequest;
import com.example.api.auth.entity.RefreshToken;
import com.example.api.auth.repository.RefreshTokenRepository;
import com.example.api.shared.security.JwtTokenProvider;
import com.example.api.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;

    private LogoutUseCase logoutUseCase;

    @BeforeEach
    void setUp() {
        logoutUseCase = new LogoutUseCase(refreshTokenRepository, jwtTokenProvider);
    }

    @Test
    void execute_shouldRevokeToken_whenTokenExists() {
        User user = new User();
        user.setId(UUID.randomUUID());

        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setUser(user);
        token.setRevoked(false);
        token.setExpiresAt(Instant.now().plusSeconds(3600));

        when(jwtTokenProvider.hashToken("raw-token")).thenReturn("hashed");
        when(refreshTokenRepository.findByTokenHash("hashed")).thenReturn(Optional.of(token));

        logoutUseCase.execute(new RefreshRequest("raw-token"));

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void execute_shouldDoNothing_whenTokenNotFound() {
        when(jwtTokenProvider.hashToken("unknown")).thenReturn("hashed-unknown");
        when(refreshTokenRepository.findByTokenHash("hashed-unknown")).thenReturn(Optional.empty());

        logoutUseCase.execute(new RefreshRequest("unknown"));

        verify(refreshTokenRepository, never()).save(any());
    }
}

package com.example.api.auth.usecase;

import com.example.api.auth.dto.RefreshRequest;
import com.example.api.auth.dto.TokenResponse;
import com.example.api.auth.entity.RefreshToken;
import com.example.api.auth.repository.RefreshTokenRepository;
import com.example.api.shared.config.JwtProperties;
import com.example.api.shared.exception.UnauthorizedException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private JwtProperties jwtProperties;

    private RefreshTokenUseCase refreshTokenUseCase;

    @BeforeEach
    void setUp() {
        refreshTokenUseCase = new RefreshTokenUseCase(refreshTokenRepository, jwtTokenProvider, jwtProperties);
    }

    @Test
    void execute_shouldReturnNewTokens_whenRefreshTokenIsValid() {
        User user = createUser();
        RefreshToken existingToken = createRefreshToken(user, false, Instant.now().plusSeconds(3600));

        when(jwtTokenProvider.hashToken("raw-token")).thenReturn("hashed-token");
        when(refreshTokenRepository.findByTokenHash("hashed-token")).thenReturn(Optional.of(existingToken));
        when(jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail())).thenReturn("new-access");
        when(jwtTokenProvider.generateRefreshToken()).thenReturn("new-refresh");
        when(jwtTokenProvider.hashToken("new-refresh")).thenReturn("new-hashed");
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(604800000L);

        TokenResponse result = refreshTokenUseCase.execute(new RefreshRequest("raw-token"));

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        assertThat(existingToken.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(2)).save(any());
    }

    @Test
    void execute_shouldThrowUnauthorized_whenTokenNotFound() {
        when(jwtTokenProvider.hashToken("unknown")).thenReturn("hashed-unknown");
        when(refreshTokenRepository.findByTokenHash("hashed-unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenUseCase.execute(new RefreshRequest("unknown")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void execute_shouldThrowUnauthorized_whenTokenIsRevoked() {
        User user = createUser();
        RefreshToken revokedToken = createRefreshToken(user, true, Instant.now().plusSeconds(3600));

        when(jwtTokenProvider.hashToken("revoked")).thenReturn("hashed-revoked");
        when(refreshTokenRepository.findByTokenHash("hashed-revoked")).thenReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> refreshTokenUseCase.execute(new RefreshRequest("revoked")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token has been revoked");
    }

    @Test
    void execute_shouldThrowUnauthorized_whenTokenIsExpired() {
        User user = createUser();
        RefreshToken expiredToken = createRefreshToken(user, false, Instant.now().minusSeconds(3600));

        when(jwtTokenProvider.hashToken("expired")).thenReturn("hashed-expired");
        when(refreshTokenRepository.findByTokenHash("hashed-expired")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> refreshTokenUseCase.execute(new RefreshRequest("expired")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token has expired");
    }

    private User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPasswordHash("hashed");
        user.setActive(true);
        return user;
    }

    private RefreshToken createRefreshToken(User user, boolean revoked, Instant expiresAt) {
        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setUser(user);
        token.setTokenHash("hashed-token");
        token.setRevoked(revoked);
        token.setExpiresAt(expiresAt);
        return token;
    }
}

package com.example.api.auth.usecase;

import com.example.api.auth.dto.RefreshRequest;
import com.example.api.auth.dto.TokenResponse;
import com.example.api.auth.entity.RefreshToken;
import com.example.api.auth.repository.RefreshTokenRepository;
import com.example.api.shared.config.JwtProperties;
import com.example.api.shared.exception.UnauthorizedException;
import com.example.api.shared.security.JwtTokenProvider;
import com.example.api.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                               JwtTokenProvider jwtTokenProvider,
                               JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public TokenResponse execute(RefreshRequest request) {
        String tokenHash = jwtTokenProvider.hashToken(request.refreshToken());

        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (existingToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (existingToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        User user = existingToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String newRawRefreshToken = jwtTokenProvider.generateRefreshToken();

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUser(user);
        newRefreshToken.setTokenHash(jwtTokenProvider.hashToken(newRawRefreshToken));
        newRefreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()));
        newRefreshToken.setRevoked(false);
        refreshTokenRepository.save(newRefreshToken);

        log.info("Token refreshed for user {}", user.getId());
        return new TokenResponse(newAccessToken, newRawRefreshToken);
    }
}

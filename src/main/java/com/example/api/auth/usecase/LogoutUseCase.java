package com.example.api.auth.usecase;

import com.example.api.auth.dto.RefreshRequest;
import com.example.api.auth.repository.RefreshTokenRepository;
import com.example.api.shared.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository,
                         JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void execute(RefreshRequest request) {
        String tokenHash = jwtTokenProvider.hashToken(request.refreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("Refresh token revoked for user {}", token.getUser().getId());
                });
    }
}

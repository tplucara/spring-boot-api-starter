package com.example.api.auth.usecase;

import com.example.api.auth.dto.LoginRequest;
import com.example.api.auth.dto.TokenResponse;
import com.example.api.auth.entity.RefreshToken;
import com.example.api.auth.repository.RefreshTokenRepository;
import com.example.api.shared.config.JwtProperties;
import com.example.api.shared.exception.UnauthorizedException;
import com.example.api.shared.security.JwtTokenProvider;
import com.example.api.user.entity.User;
import com.example.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    public LoginUseCase(UserRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        JwtTokenProvider jwtTokenProvider,
                        JwtProperties jwtProperties,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TokenResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(jwtTokenProvider.hashToken(rawRefreshToken));
        refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        log.info("User {} authenticated successfully", user.getId());
        return new TokenResponse(accessToken, rawRefreshToken);
    }
}

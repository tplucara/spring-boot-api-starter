package com.example.api.auth.repository;

import com.example.api.IntegrationTestBase;
import com.example.api.auth.entity.RefreshToken;
import com.example.api.user.entity.User;
import com.example.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class RefreshTokenRepositoryIntegrationTest extends IntegrationTestBase {

    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("refresh-test@example.com");
        testUser.setPasswordHash("hashed");
        testUser.setActive(true);
        testUser = userRepository.save(testUser);
    }

    @Test
    void findByTokenHash_shouldReturnToken_whenHashExists() {
        RefreshToken token = createToken("hash-123", false);
        refreshTokenRepository.save(token);

        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash("hash-123");

        assertThat(found).isPresent();
        assertThat(found.get().getTokenHash()).isEqualTo("hash-123");
    }

    @Test
    void findByTokenHash_shouldReturnEmpty_whenHashDoesNotExist() {
        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void revokeAllByUserId_shouldRevokeAllActiveTokens() {
        refreshTokenRepository.save(createToken("token-1", false));
        refreshTokenRepository.save(createToken("token-2", false));
        refreshTokenRepository.save(createToken("token-3", true));

        refreshTokenRepository.revokeAllByUserId(testUser.getId());
        refreshTokenRepository.flush();

        assertThat(refreshTokenRepository.findByTokenHash("token-1").get().isRevoked()).isTrue();
        assertThat(refreshTokenRepository.findByTokenHash("token-2").get().isRevoked()).isTrue();
        assertThat(refreshTokenRepository.findByTokenHash("token-3").get().isRevoked()).isTrue();
    }

    private RefreshToken createToken(String hash, boolean revoked) {
        RefreshToken token = new RefreshToken();
        token.setUser(testUser);
        token.setTokenHash(hash);
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        token.setRevoked(revoked);
        return token;
    }
}

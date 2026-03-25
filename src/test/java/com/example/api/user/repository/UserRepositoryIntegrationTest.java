package com.example.api.user.repository;

import com.example.api.IntegrationTestBase;
import com.example.api.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        User user = new User();
        user.setEmail("integration@example.com");
        user.setPasswordHash("hashed-password");
        user.setActive(true);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("integration@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("integration@example.com");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setPasswordHash("hashed-password");
        user.setActive(true);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("exists@example.com")).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        assertThat(userRepository.existsByEmail("notfound@example.com")).isFalse();
    }
}

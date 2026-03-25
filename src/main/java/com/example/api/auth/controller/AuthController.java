package com.example.api.auth.controller;

import com.example.api.auth.dto.LoginRequest;
import com.example.api.auth.dto.RefreshRequest;
import com.example.api.auth.dto.TokenResponse;
import com.example.api.auth.usecase.LoginUseCase;
import com.example.api.auth.usecase.LogoutUseCase;
import com.example.api.auth.usecase.RefreshTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and obtain tokens")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse response = refreshTokenUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        logoutUseCase.execute(request);
        return ResponseEntity.noContent().build();
    }
}

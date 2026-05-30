package com.agency.backend.modules.auth.controller;

import com.agency.backend.common.dto.ApiResponse;
import com.agency.backend.config.RateLimitConfig;
import com.agency.backend.modules.auth.dto.LoginRequest;
import com.agency.backend.modules.auth.dto.LoginResponse;
import com.agency.backend.modules.auth.dto.RegisterRequest;
import com.agency.backend.modules.auth.service.AuthService;
import com.agency.backend.modules.users.entity.User;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Auth endpoints — login and register")
public class AuthController {

    private final AuthService authService;
    private final RateLimitConfig rateLimitConfig;

    public AuthController(AuthService authService, RateLimitConfig rateLimitConfig) {
        this.authService = authService;
        this.rateLimitConfig = rateLimitConfig;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
                                                            HttpServletRequest httpRequest) {
        String ip = rateLimitConfig.extractIp(httpRequest);
        Bucket bucket = rateLimitConfig.resolveLoginBucket(ip);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("Too many login attempts. Please try again later."));
        }
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new admin user (admin only)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", data));
    }

    @GetMapping("/healthz")
    @Operation(summary = "Health check")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("API is running", "OK"));
    }
}

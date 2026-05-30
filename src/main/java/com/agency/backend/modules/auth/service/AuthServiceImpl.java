package com.agency.backend.modules.auth.service;

import com.agency.backend.common.exception.BadRequestException;
import com.agency.backend.common.exception.DuplicateResourceException;
import com.agency.backend.config.JwtTokenProvider;
import com.agency.backend.modules.auth.dto.LoginRequest;
import com.agency.backend.modules.auth.dto.LoginResponse;
import com.agency.backend.modules.auth.dto.RegisterRequest;
import com.agency.backend.modules.users.entity.Role;
import com.agency.backend.modules.users.entity.User;
import com.agency.backend.modules.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           UserService userService,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User user = (User) auth.getPrincipal();
            String token = jwtTokenProvider.generateToken(user);
            log.info("Login success for user: {}", request.getEmail());
            return LoginResponse.builder()
                    .accessToken(token)
                    .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                    .build();
        } catch (BadCredentialsException ex) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new BadRequestException("Invalid email or password");
        }
    }

    @Override
    public User register(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.ADMIN)
                .build();
        User saved = userService.save(user);
        log.info("Registered new admin user: {}", saved.getEmail());
        return saved;
    }
}

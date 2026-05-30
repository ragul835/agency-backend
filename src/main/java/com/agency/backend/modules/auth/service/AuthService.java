package com.agency.backend.modules.auth.service;

import com.agency.backend.modules.auth.dto.LoginRequest;
import com.agency.backend.modules.auth.dto.LoginResponse;
import com.agency.backend.modules.auth.dto.RegisterRequest;
import com.agency.backend.modules.users.entity.User;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    User register(RegisterRequest request);
}

package com.agency.backend.modules.users.service;

import com.agency.backend.modules.users.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}

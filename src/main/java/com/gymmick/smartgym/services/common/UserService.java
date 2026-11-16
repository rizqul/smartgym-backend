package com.gymmick.smartgym.services.common;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.gymmick.smartgym.entity.User;

public interface UserService extends UserDetailsService {

    void save(User user);

    User findByEmail(String email);

    boolean existsByEmail(String email);
}

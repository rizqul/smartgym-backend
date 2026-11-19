package com.gymmick.smartgym.services.common;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.gymmick.smartgym.dto.UserUpdatePasswordRequest;
import com.gymmick.smartgym.dto.UserUpdateProfileRequest;
import com.gymmick.smartgym.entity.User;

public interface UserService extends UserDetailsService {

    User save(User user);

    User findById(UUID id);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User updateProfile(UUID id, UserUpdateProfileRequest request);

    User updatePassword(String email, UserUpdatePasswordRequest request);
}

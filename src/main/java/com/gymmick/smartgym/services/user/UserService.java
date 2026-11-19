package com.gymmick.smartgym.services.user;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.gymmick.smartgym.dto.user.UserPasswordUpdateRequest;
import com.gymmick.smartgym.dto.user.UserProfileUpdateRequest;
import com.gymmick.smartgym.entity.User;

public interface UserService extends UserDetailsService {

    User updateProfile(User user, UserProfileUpdateRequest request);

    User updatePassword(User user, UserPasswordUpdateRequest request);

    User findById(UUID id);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}

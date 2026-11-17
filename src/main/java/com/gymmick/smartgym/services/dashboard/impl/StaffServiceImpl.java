package com.gymmick.smartgym.services.dashboard.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.dashboard.StaffCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.services.dashboard.StaffService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaffServiceImpl implements StaffService {

  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  @Override
  @Transactional
  public void createStaff(StaffCreateRequest request) {

    User user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .phoneNumber(request.getPhoneNumber())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(UserRole.STAFF)
        .build();

    userService.save(user);
  }

  @Override
  @Transactional
  public void deleteStaff(String id) {

    UUID userId = UUID.fromString(id);
    User user = userService.findById(userId);

    user.setDeletedAt(Instant.now());
    userService.save(user);
  }

}

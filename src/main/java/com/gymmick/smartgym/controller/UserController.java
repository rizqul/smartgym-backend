package com.gymmick.smartgym.controller;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.UserUpdatePasswordRequest;
import com.gymmick.smartgym.dto.UserUpdateProfileRequest;
import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserMapping userMapping;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<ApiResponse<UserResponse>> profile(@AuthenticationPrincipal User user) {

    UserResponse userResponse = userMapping.toResponse(user);

    return ApiResponse.success(HttpStatus.OK, userResponse);
  }

  @PutMapping(path = "/profile")
  public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@AuthenticationPrincipal User user,
      @RequestBody UserUpdateProfileRequest request) {

    userService.updateProfile(user.getId(), request);
    String message = "update user berhasil";

    return ApiResponse.success(HttpStatus.OK, message);
  }

  @PutMapping(path = "/password")
  public ResponseEntity<ApiResponse<UserResponse>> updatePassword(@AuthenticationPrincipal User user,
      @Valid @RequestBody UserUpdatePasswordRequest request) {

    userService.updatePassword(user.getEmail(), request);
    String message = "update password user berhasil";

    return ApiResponse.success(HttpStatus.OK, message);
  }
}

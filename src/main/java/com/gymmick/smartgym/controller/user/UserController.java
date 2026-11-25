package com.gymmick.smartgym.controller.user;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.services.user.UserService;
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

  @PutMapping(path = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<String>> updateProfile(@AuthenticationPrincipal User user,
      @Valid @ModelAttribute UserProfileUpdateRequest request) {

    userService.updateProfile(user, request);
    String message = "Berhasil Memperbaharui Profil!";

    return ApiResponse.success(HttpStatus.OK, message);
  }

  @PutMapping(path = "/password")
  public ResponseEntity<ApiResponse<String>> updatePassword(@AuthenticationPrincipal User user,
      @Valid @RequestBody UserPasswordUpdateRequest request) {

    userService.updatePassword(user, request);
    String message = "Berhasil Memperbaharui Kata Sandi!";

    return ApiResponse.success(HttpStatus.OK, message);
  }
}

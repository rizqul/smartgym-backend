package com.gymmick.smartgym.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

  private final UserMapping userMapping;

  @GetMapping
  public ResponseEntity<ApiResponse<UserResponse>> getAllStaff() {

    return null;
  }

}

package com.gymmick.smartgym.controller.dashboard.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.TrainerCreateRequest;
import com.gymmick.smartgym.services.dashboard.TrainerService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/trainer")
@RequiredArgsConstructor
public class AdminTrainerController {

  private final TrainerService trainerService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> createTrainer(TrainerCreateRequest request) {

    trainerService.createTrainer(request);

    String message = "trainer berhasil dibuat";
    return ApiResponse.success(HttpStatus.CREATED, message);
  }
}

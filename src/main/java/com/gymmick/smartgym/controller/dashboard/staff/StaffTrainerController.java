package com.gymmick.smartgym.controller.dashboard.staff;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.trainer.TrainerResponse;
import com.gymmick.smartgym.dto.trainer.TrainerSearchRequest;
import com.gymmick.smartgym.dto.trainer.TrainerSlotFilterRequest;
import com.gymmick.smartgym.dto.trainer.TrainerSlotResponse;
import com.gymmick.smartgym.services.user.TrainerService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff/dashboard/trainers")
@RequiredArgsConstructor
public class StaffTrainerController {

  private final TrainerService trainerService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<TrainerResponse>>> findAll(TrainerSearchRequest request) {
    PaginationResponse<TrainerResponse> response = trainerService.findAllTrainer(request);

    return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
  }

  @GetMapping(path = "/slot/{id}")
  public ResponseEntity<ApiResponse<List<TrainerSlotResponse>>> findSlotByTrainerId(@PathVariable String id,
      TrainerSlotFilterRequest request) {

    List<TrainerSlotResponse> trainers = trainerService.findSlotByTrainerIdAndDate(id, request);

    return ApiResponse.success(HttpStatus.OK, trainers);
  }

}

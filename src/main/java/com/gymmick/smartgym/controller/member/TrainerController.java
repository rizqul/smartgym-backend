package com.gymmick.smartgym.controller.member;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.services.user.*;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerBookingService trainerBookingService;

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

    @PostMapping(path = "/booking")
    public ResponseEntity<ApiResponse<String>> bookedTrainer(@AuthenticationPrincipal User user,
            @Valid @RequestBody TrainerTransactionCreateRequest request) {

        String paymentUrl = trainerBookingService.createBookedTrainer(user, request);

        return ApiResponse.success(HttpStatus.OK, "OK", paymentUrl);
    }
}

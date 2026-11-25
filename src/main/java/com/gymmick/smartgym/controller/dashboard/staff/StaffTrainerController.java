package com.gymmick.smartgym.controller.dashboard.staff;

import java.time.*;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.statistic.TrainerCountResponse;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.services.trainer.*;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff/dashboard/trainers")
@RequiredArgsConstructor
public class StaffTrainerController {

    private final TrainerService trainerService;
    private final TrainerBookingService trainerBookingService;

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TrainerResponse>>> findAll(TrainerSearchRequest request) {
        PaginationResponse<TrainerResponse> response = trainerService.findAllTrainer(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @GetMapping(path = "/booking")
    public ResponseEntity<ApiResponse<List<TrainerBookingResponse>>> findAllBooking(
            TrainerBookingSearchRequest request) {

        PaginationResponse<TrainerBookingResponse> response = trainerBookingService.findAllBooking(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @GetMapping(path = "/slot/{id}")
    public ResponseEntity<ApiResponse<List<TrainerSlotResponse>>> findSlotByTrainerId(@PathVariable String id,
            TrainerSlotFilterRequest request) {

        List<TrainerSlotResponse> trainers = trainerService.findSlotByTrainerIdAndDate(id, request);

        return ApiResponse.success(HttpStatus.OK, trainers);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<ApiResponse<TrainerCountResponse>> countTrainer() {

        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        Instant startOfDay = today.atStartOfDay(zone).toInstant();
        Instant endOfDay = today.atTime(LocalTime.MAX).atZone(zone).toInstant();

        Long totalTrainer = trainerService.countAllTrainer();
        Long totalTrainerThatHasSlotOnDate = trainerService.countAllTrainerThatHasSlotDaily(startOfDay, endOfDay);
        Long totalSlotOnDate = trainerService.countAllSlotDaily(startOfDay, endOfDay);
        Long totalTransactionBookedTrainer = transactionService.countTotalIncomeByType(TransactionType.PT_BOOKING);

        TrainerCountResponse response = TrainerCountResponse.builder()
                .countTrainer(totalTrainer)
                .countTrainerWithSlot(totalTrainerThatHasSlotOnDate)
                .countSlot(totalSlotOnDate)
                .sumTransactionBookedTrainer(totalTransactionBookedTrainer)
                .build();

        return ApiResponse.success(HttpStatus.OK, response);
    }
}

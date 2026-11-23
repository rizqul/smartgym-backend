package com.gymmick.smartgym.controller.dashboard.staff;

import java.util.UUID;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.attendance.*;
import com.gymmick.smartgym.services.attendance.AttendanceService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff/dashboard/attendances")
@RequiredArgsConstructor
public class StaffAttendanceController {

    private final AttendanceService checkinService;

    @PostMapping("/qr-token")
    public ResponseEntity<ApiResponse<AttendanceTokenResponse>> generateQrToken() {

        AttendanceTokenResponse response = checkinService.generateQrToken();

        String message = "QR check-in berhasil dibuat!";
        return ApiResponse.success(HttpStatus.CREATED, message, response);
    }

    @PostMapping("/check-out/{id}")
    public ResponseEntity<ApiResponse<AttendingResponse>> checkOutMember(@PathVariable("id") UUID memberId) {

        AttendingResponse response = checkinService.checkOut(memberId);

        String message = "Berhasil melakukan check-out!";
        return ApiResponse.success(HttpStatus.OK, message, response);
    }
}

package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.attendance.*;
import com.gymmick.smartgym.services.attendance.AttendanceService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/attendances")
@RequiredArgsConstructor
public class AdminAttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendingResponse>>> findRecentAttending(@RequestParam String limit) {

        List<AttendingResponse> responses = attendanceService.findRecentAttending(limit);
        return ApiResponse.success(HttpStatus.OK, responses);
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AttendanceSummaryResponse>> getSummary() {
        AttendanceSummaryResponse response = attendanceService.getSummary();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @PostMapping("/qr-token")
    public ResponseEntity<ApiResponse<AttendanceTokenResponse>> generateQrToken() {

        AttendanceTokenResponse response = attendanceService.generateQrToken();

        String message = "Berhasil membuat token check-in!";
        return ApiResponse.success(HttpStatus.CREATED, message, response);
    }

    @PostMapping("/check-out/{id}")
    public ResponseEntity<ApiResponse<AttendingResponse>> checkOutMember(@PathVariable("id") UUID memberId) {

        AttendingResponse response = attendanceService.checkOut(memberId);

        String message = "Berhasil melakukan check-out!";
        return ApiResponse.success(HttpStatus.OK, message, response);
    }
}

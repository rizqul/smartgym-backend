package com.gymmick.smartgym.controller.dashboard.staff;

import java.util.*;

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

    @GetMapping("/summary/chart")
    public ResponseEntity<ApiResponse<AttendanceHourlyChartResponse>> getHourlyChart() {

        AttendanceHourlyChartResponse response = attendanceService.getHourlyChart();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @PostMapping("/qr-token")
    public ResponseEntity<ApiResponse<AttendanceTokenResponse>> generateQrToken() {

        AttendanceTokenResponse response = attendanceService.generateQrToken();

        String message = "Create check-in QR token successfully!";
        return ApiResponse.success(HttpStatus.CREATED, message, response);
    }

    @PostMapping("/check-out/{id}")
    public ResponseEntity<ApiResponse<AttendingResponse>> checkOutMember(@PathVariable("id") UUID memberId) {

        AttendingResponse response = attendanceService.checkOut(memberId);

        String message = "Check-out member succesfully!";
        return ApiResponse.success(HttpStatus.OK, message, response);
    }
}

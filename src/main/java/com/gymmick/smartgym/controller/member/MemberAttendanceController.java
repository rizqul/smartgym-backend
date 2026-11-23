package com.gymmick.smartgym.controller.member;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.attendance.AttendanceFilterRequest;
import com.gymmick.smartgym.dto.attendance.AttendingRequest;
import com.gymmick.smartgym.dto.attendance.AttendingResponse;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.services.attendance.AttendanceService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/attendance")
@RequiredArgsConstructor
public class MemberAttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendingResponse>>> findMyCheckins(@AuthenticationPrincipal User member,
            AttendanceFilterRequest request) {

        PaginationResponse<AttendingResponse> response = attendanceService.findAllMyAttendance(member, request);
        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    // @GetMapping
    // public ResponseEntity<ApiResponse<AttendingResponse>>
    // findActiveCheckin(@AuthenticationPrincipal User member) {

    // return checkinService.findActiveCheckin(member)
    // .map(response -> ApiResponse.success(HttpStatus.OK, response))
    // .orElseGet(() -> ApiResponse.success(
    // HttpStatus.OK, "Tidak ada check-in aktif.", (AttendingResponse) null));
    // }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendingResponse>> checkIn(@Valid @RequestBody AttendingRequest request,
            @AuthenticationPrincipal User member) {

        AttendingResponse response = attendanceService.checkIn(request.getToken(), member);

        String message = "Berhasil melakukan check-in!";
        return ApiResponse.success(HttpStatus.CREATED, message, response);
    }

    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<AttendingResponse>> checkOut(@AuthenticationPrincipal User member) {

        AttendingResponse response = attendanceService.checkOut(member.getId());

        String message = "Berhasil melakukan check-out!";
        return ApiResponse.success(HttpStatus.OK, message, response);
    }
}

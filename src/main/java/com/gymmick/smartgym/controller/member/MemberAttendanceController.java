package com.gymmick.smartgym.controller.member;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.attendance.*;
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
  public ResponseEntity<ApiResponse<List<AttendingResponse>>> findMyAttending(@AuthenticationPrincipal User member,
      AttendanceFilterRequest request) {

    PaginationResponse<AttendingResponse> response = attendanceService.findAllMyAttendance(member, request);
    return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
  }

  @GetMapping("/status")
  public ResponseEntity<ApiResponse<AttendingResponse>> findMyActiveAttending(@AuthenticationPrincipal User member) {

    AttendingResponse response = attendanceService.findActiveCheckIn(member);
    return ApiResponse.success(HttpStatus.OK, response);
  }

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

  @GetMapping("/summary")
  public ResponseEntity<ApiResponse<MemberAttendanceSummaryResponse>> getMyAttendanceSummary(
      @AuthenticationPrincipal User member) {

    MemberAttendanceSummaryResponse response = attendanceService.getMyAttendanceSummary(member);

    return ApiResponse.success(HttpStatus.OK, response);
  }
}

package com.gymmick.smartgym.controller.dashboard.admin;

import java.time.*;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.attendance.*;
import com.gymmick.smartgym.dto.statistic.*;
import com.gymmick.smartgym.dto.transaction.TransactionResponse;
import com.gymmick.smartgym.services.attendance.AttendanceService;
import com.gymmick.smartgym.services.trainer.TrainerService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.services.user.*;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/summary")
@RequiredArgsConstructor
public class AdminStatisticController {

    private final MemberService memberService;
    private final AttendanceService attendanceService;

    private final TrainerService trainerService;
    private final StaffService staffService;

    private final TransactionService transactionService;

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<DashboardCountResponse>> getCountStats() {
        int targetYear = LocalDate.now(ZoneId.systemDefault()).getYear();

        Long allMember = memberService.countAllMember();
        Long activeMember = memberService.countAllActiveMember();

        Long totalTrainer = trainerService.countAllTrainer();
        Long staffCount = staffService.countStaff();

        Long incomeYearly = transactionService.sumTotalIncomeYearly(targetYear, ZoneId.systemDefault());

        DashboardCountResponse response = DashboardCountResponse.builder()
                .allMember(allMember)
                .activeMember(activeMember)
                .totalTrainer(totalTrainer)
                .staffCount(staffCount)
                .incomeYearly(incomeYearly)
                .build();

        return ApiResponse.success(HttpStatus.OK, response);

    }

    @GetMapping("/chart")
    public ResponseEntity<ApiResponse<AttendanceHourlyChartResponse>> getHourlyChart() {
        AttendanceHourlyChartResponse response = attendanceService.getHourlyChart();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getRecentSummary() {
        List<AttendingResponse> attendanceResponse = attendanceService.findRecentAttending("5");
        List<TransactionResponse> transactionResponses = transactionService.findRecentTransaction("5");

        DashboardSummaryResponse response = DashboardSummaryResponse.builder()
                .attendanceResponse(attendanceResponse)
                .transactionResponses(transactionResponses)
                .build();

        return ApiResponse.success(HttpStatus.OK, response);
    }
}

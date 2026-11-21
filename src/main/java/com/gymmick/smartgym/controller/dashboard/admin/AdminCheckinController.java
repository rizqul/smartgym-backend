package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.checkin.CheckinResponse;
import com.gymmick.smartgym.dto.checkin.CheckinSummaryResponse;
import com.gymmick.smartgym.dto.checkin.CheckinTokenResponse;
import com.gymmick.smartgym.services.checkin.CheckinService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/checkins")
@RequiredArgsConstructor
public class AdminCheckinController {

    private final CheckinService checkinService;

    @PostMapping("/qr-token")
    public ResponseEntity<ApiResponse<CheckinTokenResponse>> generateQrToken() {
        return ApiResponse.success(HttpStatus.CREATED, "QR check-in berhasil dibuat.",
                checkinService.generateQrToken());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CheckinResponse>>> findRecentCheckins(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success(HttpStatus.OK, checkinService.findRecentCheckins(limit));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<CheckinSummaryResponse>> getSummary() {
        return ApiResponse.success(HttpStatus.OK, checkinService.getSummary());
    }
}

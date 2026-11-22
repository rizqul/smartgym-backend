package com.gymmick.smartgym.controller.dashboard.staff;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.checkin.CheckinTokenResponse;
import com.gymmick.smartgym.dto.checkin.CheckinResponse;
import com.gymmick.smartgym.services.checkin.CheckinService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff/dashboard/checkins")
@RequiredArgsConstructor
public class StaffCheckinController {

  private final CheckinService checkinService;

  @PostMapping("/qr-token")
  public ResponseEntity<ApiResponse<CheckinTokenResponse>> generateQrToken() {
    return ApiResponse.success(HttpStatus.CREATED, "QR check-in berhasil dibuat.",
        checkinService.generateQrToken());
  }

  @PostMapping("/{memberId}/checkout")
  public ResponseEntity<ApiResponse<CheckinResponse>> checkOutMember(@PathVariable UUID memberId) {
    CheckinResponse response = checkinService.checkOut(memberId);
    return ApiResponse.success(HttpStatus.OK, "Check-out member berhasil.", response);
  }

}

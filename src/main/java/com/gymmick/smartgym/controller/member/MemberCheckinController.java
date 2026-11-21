package com.gymmick.smartgym.controller.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.checkin.CheckinRequest;
import com.gymmick.smartgym.dto.checkin.CheckinResponse;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.services.checkin.CheckinService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/checkins")
@RequiredArgsConstructor
public class MemberCheckinController {

    private final CheckinService checkinService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckinResponse>> checkIn(
            @Valid @RequestBody CheckinRequest request,
            @AuthenticationPrincipal User member) {
        CheckinResponse response = checkinService.checkIn(request.getToken(), member);
        return ApiResponse.success(HttpStatus.CREATED, "Check-in berhasil.", response);
    }
}

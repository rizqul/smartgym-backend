package com.gymmick.smartgym.controller.member;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.member.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.services.dashboard.MembershipService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/member/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> buyMembership(
            @Valid @RequestBody MembershipTransactionCreateRequest request, @AuthenticationPrincipal User user) {

        String paymentUrl = membershipService.createMembership(request, user);

        return ApiResponse.success(HttpStatus.OK, "OK", paymentUrl);
    }

}

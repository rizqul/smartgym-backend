package com.gymmick.smartgym.controller.member;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.dashboard.MembershipPackageResponse;
import com.gymmick.smartgym.dto.member.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.services.dashboard.MembershipService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<MembershipType, List<MembershipPackageResponse>>>> findAllPackage() {
        Map<MembershipType, List<MembershipPackageResponse>> responses = membershipService.findAllPackage();

        return ApiResponse.success(HttpStatus.OK, "OK", responses);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> buyMembership(
            @Valid @RequestBody MembershipTransactionCreateRequest request, @AuthenticationPrincipal User user) {

        String paymentUrl = membershipService.buyMembership(request, user);

        return ApiResponse.success(HttpStatus.OK, "OK", paymentUrl);
    }

}

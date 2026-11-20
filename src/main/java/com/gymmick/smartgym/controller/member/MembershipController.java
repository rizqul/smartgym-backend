package com.gymmick.smartgym.controller.member;

import java.util.*;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.membership.MembershipPackageResponse;
import com.gymmick.smartgym.dto.membership.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.services.membership.MembershipService;
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

    @PostMapping(path = "/order")
    public ResponseEntity<ApiResponse<String>> buyMembership(
            @Valid @RequestBody MembershipTransactionCreateRequest request, @AuthenticationPrincipal User user) {

        String paymentUrl = membershipService.buyMembership(user, request);

        return ApiResponse.success(HttpStatus.OK, "OK", paymentUrl);
    }
}

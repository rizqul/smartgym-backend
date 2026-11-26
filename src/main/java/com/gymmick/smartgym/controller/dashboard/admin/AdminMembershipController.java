package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.membership.*;
import com.gymmick.smartgym.dto.statistic.MembershipCountResponse;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/memberships")
@RequiredArgsConstructor
public class AdminMembershipController {

    private final MembershipService membershipService;
    private final TransactionService transactionService;

    @GetMapping(path = "/count")
    public ResponseEntity<ApiResponse<MembershipCountResponse>> countMembership() {

        Long countMembershipPackage = membershipService.countMembershipPackage();
        Long totalMembershipIncome = transactionService.countTotalIncomeByType(TransactionType.MEMBERSHIP);

        MembershipCountResponse response = MembershipCountResponse.builder()
                .totalMembershipIncome(totalMembershipIncome)
                .countMembershipPackage(countMembershipPackage)
                .build();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @GetMapping(path = "/packages")
    public ResponseEntity<ApiResponse<Map<MembershipType, List<MembershipPackageResponse>>>> findAllMemberPackage() {

        Map<MembershipType, List<MembershipPackageResponse>> responses = membershipService.findAllPackage();

        return ApiResponse.success(HttpStatus.OK, responses);
    }

    @PostMapping(path = "/packages/create")
    public ResponseEntity<ApiResponse<String>> createMemberPackage(
            @Valid @RequestBody MembershipPackageCreateRequest request) {

        membershipService.createPackage(request);

        String message = "Added membership package successfully!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @PatchMapping(path = "/packages/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateMemberPackage(@PathVariable String id,
            @Valid @RequestBody MembershipPackageUpdateRequest request) {

        membershipService.updatePackage(id, request);

        String message = "Updated membership package successfully!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @DeleteMapping(path = "/packages/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMemberPackage(@PathVariable String id) {

        membershipService.deletePackage(id);

        String message = "Deleted membership package successfully!";
        return ApiResponse.success(HttpStatus.OK, message);
    }
}

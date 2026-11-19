package com.gymmick.smartgym.controller.member;

import java.util.*;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.membership.MembershipPackageResponse;
import com.gymmick.smartgym.dto.membership.MembershipTransactionCreateRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<MembershipType, List<MembershipPackageResponse>>>> findAllPackage() {
        Map<MembershipType, List<MembershipPackageResponse>> responses = membershipService.findAllPackage();

        return ApiResponse.success(HttpStatus.OK, "OK", responses);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> buyMembership(
            @Valid @RequestBody MembershipTransactionCreateRequest request, @AuthenticationPrincipal User user) {

        String paymentUrl = membershipService.buyMembership(user, request);

        return ApiResponse.success(HttpStatus.OK, "OK", paymentUrl);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> cancelTransaction(@RequestParam String orderId) {

        try {
            transactionService.cancelTransaction(orderId);

            String message = "Berhasil Membatalkan Transaksi!";
            return ApiResponse.success(HttpStatus.OK, message);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}

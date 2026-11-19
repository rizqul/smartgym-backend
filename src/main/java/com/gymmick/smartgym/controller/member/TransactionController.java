package com.gymmick.smartgym.controller.member;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findAll(@AuthenticationPrincipal User user,
            TransactionFilterRequest request) {

        PaginationResponse<TransactionResponse> response = transactionService.findAllMyTransaction(user, request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<String>> cancelTransaction(@PathVariable("id") String orderId) {

        try {
            transactionService.cancelTransaction(orderId);

            String message = "Berhasil Membatalkan Transaksi!";
            return ApiResponse.success(HttpStatus.OK, message);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}

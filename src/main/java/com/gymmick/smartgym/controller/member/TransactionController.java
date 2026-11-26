package com.gymmick.smartgym.controller.member;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.services.transaction.MidtransService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;
import com.midtrans.httpclient.error.MidtransError;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final MidtransService midtransService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findAll(@AuthenticationPrincipal User user,
            TransactionSearchRequest request) {

        PaginationResponse<TransactionResponse> response = transactionService.findAllMyTransaction(user, request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<Transaction>> detailTransaction(@PathVariable("id") String orderId) {

        Transaction transaction = transactionService.detailTransaction(orderId);

        return ApiResponse.success(HttpStatus.OK, transaction);
    }

    @GetMapping(path = "/check/{id}")
    public ResponseEntity<ApiResponse<Transaction>> checkTransaction(@PathVariable("id") String orderId)
            throws MidtransError {

        Transaction transaction = midtransService.checkTransaction(orderId);

        return ApiResponse.success(HttpStatus.OK, transaction);
    }

    @PostMapping(path = "/cancel/{id}")
    public ResponseEntity<ApiResponse<String>> cancelTransaction(@PathVariable("id") String orderId,
            @RequestBody TransactionCancelRequest request) {

        midtransService.cancelTransaction(orderId, request);

        String message = "Berhasil Membatalkan Transaksi!";
        return ApiResponse.success(HttpStatus.OK, message);
    }
}

package com.gymmick.smartgym.controller.member;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

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

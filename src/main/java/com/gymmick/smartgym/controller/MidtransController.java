package com.gymmick.smartgym.controller;

import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.services.dashboard.MembershipService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/midtrans")
@RequiredArgsConstructor
public class MidtransController {

    private final MidtransCoreApi midtransCoreApi;

    private final MembershipService membershipService;
    private final TransactionService transactionService;

    @PostMapping(path = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    public String notification(@RequestBody Map<String, Object> response) {

        try {
            if (!response.isEmpty()) {
                String orderId = response.get("order_id").toString();
                Map<String, Object> transactionResult = midtransCoreApi.checkTransaction(orderId).toMap();

                String transactionStatus = transactionResult.get("transaction_status").toString();
                Transaction transaction = transactionService.updateTransaction(orderId, transactionStatus);

                if (transaction.getType().equals(TransactionType.MEMBERSHIP)
                        && transactionStatus.equals("settlement")) {

                    membershipService.activationMembership(transaction);
                }
            }

            return "OK";
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(path = "/membership")
    public ResponseEntity<ApiResponse<String>> validationOrder(@RequestParam String order_id,
            String transaction_status) throws MidtransError {

        Map<String, Object> transactionResult = midtransCoreApi.checkTransaction(order_id).toMap();
        String transactionStatus = transactionResult.get("transaction_status").toString();

        if (transactionStatus.equals("pending")) {
            throw new BadRequestException("Menunggu Pembayaran!");
        }

        if (transactionStatus.equals("expire")) {
            throw new BadRequestException("Tautan Pembayaran Telah Kedaluwarsa!");
        }

        String message = "Aktivasi Membership Berhasil!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

}

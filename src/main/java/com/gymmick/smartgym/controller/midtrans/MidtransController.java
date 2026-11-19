package com.gymmick.smartgym.controller.midtrans;

import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;

import lombok.RequiredArgsConstructor;

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
                String paymentType = transactionResult.get("payment_type").toString();

                Transaction transaction = transactionService.updateTransaction(orderId, transactionStatus, paymentType);

                if (transactionStatus.equals("settlement")) {
                    if (transaction.getType().equals(TransactionType.MEMBERSHIP)) {
                        membershipService.activationMembership(transaction);
                    }
                }
            }

            return "OK";
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(path = "/membership")
    public ResponseEntity<ApiResponse<String>> validationOrder(@RequestParam(name = "order_id") String orderId) {
        try {
            String transactionStatus = this.getTransactionStatus(orderId);

            return switch (transactionStatus) {
                case "pending" -> throw new BadRequestException("Menunggu Pembayaran!");
                case "cancel" -> throw new BadRequestException("Transaksi Telah Dibatalkan!");
                case "expire" -> throw new BadRequestException("Tautan Pembayaran Telah Kedaluwarsa!");
                case "settlement" -> ApiResponse.success(HttpStatus.OK, "Aktivasi Membership Berhasil!");
                default -> throw new BadRequestException(
                        "Terjadi Kesalahan Saat Melakukan Pengecekan Transaksi!");
            };
        } catch (MidtransError e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private String getTransactionStatus(String orderId) throws MidtransError {
        return midtransCoreApi.checkTransaction(orderId).toMap()
                .get("transaction_status")
                .toString();
    }
}

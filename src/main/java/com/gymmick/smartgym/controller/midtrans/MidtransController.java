package com.gymmick.smartgym.controller.midtrans;

import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.trainer.TrainerBookingService;
import com.gymmick.smartgym.services.transaction.MidtransService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;
import com.midtrans.httpclient.error.MidtransError;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/midtrans")
@RequiredArgsConstructor
public class MidtransController {

    private final MembershipService membershipService;
    private final TrainerBookingService trainerBookingService;
    private final TransactionService transactionService;

    private final MidtransService midtransService;

    @PostMapping(path = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    public String notification(@RequestBody Map<String, Object> response) {
        try {
            if (!response.isEmpty()) {
                String signatureKey = response.get("signature_key").toString();
                String statusCode = response.get("status_code").toString();

                String orderId = response.get("order_id").toString();
                String grossAmount = response.get("gross_amount").toString();

                Map<String, Object> transactionResult = midtransService.validateNotification(orderId, statusCode,
                        grossAmount, signatureKey);

                String transactionStatus = transactionResult.get("transaction_status").toString();
                String fraudStatus = transactionResult.get("fraud_status").toString();

                String paymentType = transactionResult.get("payment_type").toString();
                Transaction transaction = transactionService.updateTransaction(orderId, transactionStatus, paymentType);

                if (transactionStatus.equals("settlement") && fraudStatus.equals("accept")) {
                    if (transaction.getType().equals(TransactionType.MEMBERSHIP)) {
                        membershipService.activationMembership(transaction);
                    }

                    if (transaction.getType().equals(TransactionType.PT_BOOKING)) {
                        trainerBookingService.finalizeBookedTrainer(transaction);
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
            Map<String, Object> transactionResult = midtransService.checkTransactionFromMidtrans(orderId);
            String transactionStatus = transactionResult.get("transaction_status").toString();

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

    @GetMapping(path = "/pt-booking")
    public ResponseEntity<ApiResponse<String>> validationBooking(@RequestParam(name = "order_id") String orderId) {
        try {
            Map<String, Object> transactionResult = midtransService.checkTransactionFromMidtrans(orderId);
            String transactionStatus = transactionResult.get("transaction_status").toString();

            return switch (transactionStatus) {
                case "pending" -> throw new BadRequestException("Menunggu Pembayaran!");
                case "cancel" -> throw new BadRequestException("Transaksi Telah Dibatalkan!");
                case "expire" -> throw new BadRequestException("Tautan Pembayaran Telah Kedaluwarsa!");
                case "settlement" -> ApiResponse.success(HttpStatus.OK, "Booking Trainer Berhasil!");
                default -> throw new BadRequestException(
                        "Terjadi Kesalahan Saat Melakukan Pengecekan Transaksi!");
            };
        } catch (MidtransError e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}

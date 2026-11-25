package com.gymmick.smartgym.services.transaction.impl;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.*;

import com.gymmick.smartgym.dto.transaction.TransactionCancelRequest;
import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.trainer.TrainerBookingService;
import com.gymmick.smartgym.services.transaction.*;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MidtransServiceImpl implements MidtransService {

    private final MidtransCoreApi midtransCoreApi;
    private final TransactionService transactionService;

    private final TrainerSlotRepository trainerSlotRepository;
    private final TransactionRespository transactionRespository;

    private final MembershipService membershipService;
    private final TrainerBookingService trainerBookingService;

    @Qualifier("midtrans")
    private final RestClient midtransRestClient;

    @Value("${app.config.midtrans.signature-key}")
    private String midtransServerKey;

    @Override
    public Map<String, Object> validateNotification(String orderId, String statusCode, String grossAmount,
            String signatureKey) throws MidtransError {

        String calculatedSignature = Sha512DigestUtils.shaHex(orderId + statusCode + grossAmount + midtransServerKey);

        if (!MessageDigest.isEqual(calculatedSignature.getBytes(), signatureKey.getBytes())) {
            throw new BadRequestException("Gagal Memverifikasi Transaksi!");
        }

        return midtransCoreApi.checkTransaction(orderId).toMap();
    }

    @Override
    public Map<String, Object> checkTransactionFromMidtrans(String orderId) throws MidtransError {
        return midtransCoreApi.checkTransaction(orderId).toMap();
    }

    @Override
    @Transactional
    public Transaction checkTransaction(String orderId) throws MidtransError {
        Transaction transaction = transactionService.findByOrderId(orderId);

        Map<String, Object> transactionResult = this.checkTransactionFromMidtrans(orderId);
        String transactionStatus = transactionResult.get("transaction_status").toString();

        if (transactionStatus.equals("cancel")) {
            transaction.setStatus(TransactionStatus.CANCELLED);
        }

        if (transactionStatus.equals("expire")) {
            transaction.setStatus(TransactionStatus.EXPIRED);
        }

        if (transactionStatus.equals("settlement")) {
            if (transaction.getType().equals(TransactionType.MEMBERSHIP)) {
                membershipService.activationMembership(transaction);
            }

            if (transaction.getType().equals(TransactionType.PT_BOOKING)) {
                trainerBookingService.finalizeBookedTrainer(transaction);
            }

            transaction.setPaidAt(Instant.now());
            transaction.setStatus(TransactionStatus.SUCCESS);
        }

        return transactionRespository.save(transaction);
    }

    @Override
    @Transactional
    public void cancelTransaction(String orderId, TransactionCancelRequest request) {
        Transaction transaction = transactionService.findByOrderId(orderId);
        TransactionStatus transactionStatus = transaction.getStatus();

        if (transactionStatus.equals(TransactionStatus.CANCELLED)
                || transactionStatus.equals(TransactionStatus.EXPIRED)) {

            throw new BadRequestException("Transaksi ini telah dibatalkan atau kedaluwarsa!");
        }

        try {
            String response = midtransRestClient.post()
                    .uri("/snap/v1/transactions/{token}/cancel", request.getToken())
                    .body(Map.of())
                    .retrieve()
                    .body(String.class);

            System.out.println(response);
        } catch (RestClientException restClientException) {
            try {
                midtransCoreApi.cancelTransaction(orderId);
            } catch (MidtransError e) {
                throw new BadRequestException("Gagal membatalkan transaksi!");
            }
        }

        if (transaction.getType().equals(TransactionType.PT_BOOKING)) {
            trainerSlotRepository.updateStatusIfMatch(transaction.getPackageId(),
                    SlotStatus.BOOKED,
                    SlotStatus.AVAILABLE);
        }

        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRespository.save(transaction);
    }
}

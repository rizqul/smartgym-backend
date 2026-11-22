package com.gymmick.smartgym.services.transaction.impl;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.repositories.TransactionRespository;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.services.transaction.*;
import com.gymmick.smartgym.services.user.TrainerBookingService;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MidtransServiceImpl implements MidtransService {

    private final MidtransCoreApi midtransCoreApi;
    private final TransactionService transactionService;

    private final TransactionRespository transactionRespository;

    private final MembershipService membershipService;
    private final TrainerBookingService trainerBookingService;

    @Value("${app.config.midtrans.signature-key}")
    private String serverKey;

    @Override
    public Map<String, Object> validateNotification(String orderId, String statusCode, String grossAmount,
            String signatureKey) throws MidtransError {

        String calculatedSignature = Sha512DigestUtils.shaHex(orderId + statusCode + grossAmount + serverKey);

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
    public void cancelTransaction(String orderId) throws MidtransError {
        Transaction transaction = transactionService.findByOrderId(orderId);
        midtransCoreApi.cancelTransaction(orderId);

        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRespository.save(transaction);
    }
}

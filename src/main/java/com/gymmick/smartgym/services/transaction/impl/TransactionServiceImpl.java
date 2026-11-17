package com.gymmick.smartgym.services.transaction.impl;

import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.repositories.TransactionRespository;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final MidtransSnapApi midtransSnapApi;

    private final TransactionRespository transactionRespository;

    @Override
    @Transactional
    public String createTransaction(User user, TransactionData data) throws MidtransError {

        Map<String, Object> params = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", data.getOrderId());
        transactionDetails.put("gross_amount", data.getGrossAmount().toString());

        Map<String, String> callbacks = new HashMap<>();
        callbacks.put("finish", data.getCallbackUrl());
        callbacks.put("error", data.getCallbackUrl());

        params.put("transaction_details", transactionDetails);
        params.put("item_details", data.getItemDetails());
        params.put("callbacks", callbacks);

        Map<String, Object> response = midtransSnapApi.createTransaction(params).toMap();
        Transaction transaction = Transaction.builder()
                .packageId(data.getPackageId())
                .orderId(data.getOrderId())
                .paymentUrl(response.get("redirect_url").toString())
                .member(user)
                .type(data.getType())
                .amount(data.getGrossAmount())
                .build();

        transactionRespository.save(transaction);

        return response.get("redirect_url").toString();
    }

    @Override
    @Transactional
    public Transaction updateTransaction(String orderId, String status) {

        Transaction transaction = transactionRespository.findByOrderId(orderId)
                .orElseThrow(() -> new BadRequestException("Data Tidak Ditemukan!"));

        if (status.equals("expire")) {
            transaction.setStatus(TransactionStatus.EXPIRED);
        }

        if (status.equals("settlement")) {
            transaction.setPaidAt(Instant.now());
            transaction.setStatus(TransactionStatus.PROCESS);
        }

        return transactionRespository.save(transaction);
    }
}

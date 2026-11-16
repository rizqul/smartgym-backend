package com.gymmick.smartgym.services.transaction.impl;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
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

        params.put("transaction_details", transactionDetails);
        params.put("item_details", data.getItemDetails());

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

    public void updateTransactionStatus(String id, TransactionStatus status){

    }
}

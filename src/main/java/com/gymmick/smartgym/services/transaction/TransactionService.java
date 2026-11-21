package com.gymmick.smartgym.services.transaction;

import java.util.Map;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.midtrans.httpclient.error.MidtransError;

public interface TransactionService {

    Map<String, Object> checkTransactionStatus(String orderId) throws MidtransError;

    PaginationResponse<TransactionResponse> findAllTransaction(TransactionFilterRequest request);

    PaginationResponse<TransactionResponse> findAllMyTransaction(User user, TransactionFilterRequest request);

    String createTransaction(User user, TransactionData data) throws MidtransError;

    Transaction updateTransaction(String orderId, String status, String paymentType);

    void cancelTransaction(String orderId) throws MidtransError;
}

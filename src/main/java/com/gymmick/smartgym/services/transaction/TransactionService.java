package com.gymmick.smartgym.services.transaction;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.midtrans.httpclient.error.MidtransError;

public interface TransactionService {

    PaginationResponse<TransactionResponse> findAllTransaction(TransactionFilterRequest request);

    String createTransaction(User user, TransactionData data) throws MidtransError;

    Transaction updateTransaction(String orderId, String status, String paymentType);

    void cancelTransaction(String orderId) throws MidtransError;
}

package com.gymmick.smartgym.services.transaction;

import java.time.ZoneId;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.midtrans.httpclient.error.MidtransError;

public interface TransactionService {

    PaginationResponse<TransactionResponse> findAllTransaction(TransactionFilterRequest request);

    PaginationResponse<TransactionResponse> findAllMyTransaction(User user, TransactionFilterRequest request);

    Transaction findByOrderId(String orderId);

    TransactionCreate createTransaction(User user, TransactionData data) throws MidtransError;

    Transaction detailTransaction(String orderId);

    Transaction updateTransaction(String orderId, String status, String paymentType);

    Long countTotalIncomeByType(TransactionType type);

    Long countTransactionByStatus(TransactionStatus status);

    Long sumTotalIncomeYearly(int year, ZoneId zone);
}

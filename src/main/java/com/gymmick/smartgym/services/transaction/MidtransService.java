package com.gymmick.smartgym.services.transaction;

import java.util.Map;

import com.gymmick.smartgym.entity.Transaction;
import com.midtrans.httpclient.error.MidtransError;

public interface MidtransService {

    Map<String, Object> validateNotification(String orderId, String statusCode, String grossAmount,
            String signatureKey) throws MidtransError;

    Map<String, Object> checkTransactionFromMidtrans(String orderId) throws MidtransError;

    Transaction checkTransaction(String orderId) throws MidtransError;

    void cancelTransaction(String orderId, String token);
}

package com.gymmick.smartgym.services.transaction;

import java.util.Map;

import com.gymmick.smartgym.entity.Transaction;
import com.midtrans.httpclient.error.MidtransError;

public interface MidtransService {

    Map<String, Object> midtransCheckTransaction(String orderId) throws MidtransError;

    Transaction checkTransaction(String orderId) throws MidtransError;

    void cancelTransaction(String orderId) throws MidtransError;
}

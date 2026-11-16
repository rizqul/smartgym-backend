package com.gymmick.smartgym.services.transaction;

import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.User;
import com.midtrans.httpclient.error.MidtransError;

public interface TransactionService {

    String createTransaction(User user, TransactionData data) throws MidtransError;
}

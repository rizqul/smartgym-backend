package com.gymmick.smartgym.services.user;

import com.gymmick.smartgym.dto.trainer.TrainerTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.TransactionCreate;
import com.gymmick.smartgym.entity.*;

public interface TrainerBookingService {

    TransactionCreate createBookedTrainer(User member, TrainerTransactionCreateRequest request);

    void finalizeBookedTrainer(Transaction transaction);
}

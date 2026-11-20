package com.gymmick.smartgym.services.user;

import com.gymmick.smartgym.dto.trainer.TrainerTransactionCreateRequest;
import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.User;

public interface TrainerBookingService {

  String createBookedTrainer(User member, TrainerTransactionCreateRequest request);

  void finalizeBookedTrainer(Transaction transaction);

}

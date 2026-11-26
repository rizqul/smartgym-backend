package com.gymmick.smartgym.services.trainer;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.dto.transaction.TransactionCreate;
import com.gymmick.smartgym.entity.*;

public interface TrainerBookingService {

    PaginationResponse<TrainerBookingResponse> findAllBooking(TrainerBookingSearchRequest request);

    PaginationResponse<TrainerBookingResponse> findAllMyBooking(User memmber, TrainerBookingSearchRequest request);

    TransactionCreate createBookedTrainer(User member, TrainerTransactionCreateRequest request);

    void finalizeBookedTrainer(Transaction transaction);

    void refundBookedTrainer(String packageId);
}

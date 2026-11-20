package com.gymmick.smartgym.services.user.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.trainer.TrainerTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.services.user.TrainerBookingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerBookingServiceImpl implements TrainerBookingService {

    private final TrainerSlotRepository trainerSlotRepository;
    private final TrainerBookingRepository trainerBookingRepository;
    private final TransactionRespository transactionRespository;

    private final TransactionService transactionService;

    @Value("${app.config.midtrans.pt-booking.redirect-url}")
    private String ptBookedRedirectUrl;

    @Override
    @Transactional
    public String createBookedTrainer(User member, TrainerTransactionCreateRequest request) {

        try {
            Optional<Transaction> transaction = transactionRespository
                    .findFirstByMemberIdAndStatusEqualsAndTypeOrderByCreatedAtAsc(member.getId(),
                            TransactionStatus.PENDING,
                            TransactionType.PT_BOOKING);

            transaction.ifPresent(t -> {
                if (t.getStatus().equals(TransactionStatus.PENDING)) {
                    throw new BadRequestException("Silahkan Selesaikan Transaksi Sebelumnya!");
                }
            });

            UUID trainerSlotId = UUID.fromString(request.getSlotId());
            TrainerSlot trainerSlot = this.findById(trainerSlotId);

            Map<String, String> bookingDetail = new HashMap<>();
            bookingDetail.put("id", request.getSlotId());
            bookingDetail.put("name", member.getName() + "-" + trainerSlot.getTrainer().getName());
            bookingDetail.put("price", trainerSlot.getTrainer().getRate().toString());
            bookingDetail.put("quantity", "1");

            List<Map<String, String>> bookingDetails = new ArrayList<>();
            bookingDetails.add(bookingDetail);

            TransactionData transactionData = TransactionData.builder()
                    .packageId(trainerSlotId)
                    .grossAmount(trainerSlot.getTrainer().getRate())
                    .type(TransactionType.PT_BOOKING)
                    .itemDetails(bookingDetails)
                    .callbackUrl(ptBookedRedirectUrl)
                    .build();

            trainerSlot.setStatus(SlotStatus.FULL_BOOKED);
            trainerSlotRepository.save(trainerSlot);

            return transactionService.createTransaction(member, transactionData);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void finalizeBookedTrainer(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.PROCESS) {
            TrainerSlot trainerSlot = this.findById(transaction.getPackageId());

            TrainerBooking trainerBooking = TrainerBooking.builder()
                    .trainer(trainerSlot.getTrainer())
                    .member(transaction.getMember())
                    .slot(trainerSlot)
                    .transaction(transaction)
                    .build();

            trainerBookingRepository.save(trainerBooking);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRespository.save(transaction);
        }
    }

    public TrainerSlot findById(UUID id) {
        return trainerSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }
}

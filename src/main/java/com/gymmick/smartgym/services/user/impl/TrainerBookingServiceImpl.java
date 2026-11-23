package com.gymmick.smartgym.services.user.impl;

import java.time.Instant;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.trainer.TrainerTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
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
    public TransactionCreate createBookedTrainer(User member, TrainerTransactionCreateRequest request) {
        try {
            Optional<Transaction> transaction = transactionRespository
                    .findFirstByMemberIdAndStatusEqualsOrderByCreatedAtAsc(member.getId(),
                            TransactionStatus.PENDING);

            transaction.ifPresent(t -> {
                if (t.getStatus().equals(TransactionStatus.PENDING)) {
                    throw new BadRequestException("Silahkan Selesaikan Transaksi Sebelumnya!");
                }
            });

            UUID trainerSlotId = UUID.fromString(request.getSlotId());

            TrainerSlot trainerSlot = trainerSlotRepository.findByIdForUpdate(trainerSlotId)
                    .orElseThrow(() -> new ResourceNotFoundException());

            if (Instant.now().isAfter(trainerSlot.getStartTime())) {
                throw new BadRequestException("Tidak dapat melakukan booking, waktu slot sudah terlewati!");
            }

            int updatedStatus = trainerSlotRepository.updateStatusIfMatch(
                    trainerSlotId, SlotStatus.AVAILABLE, SlotStatus.BOOKED);

            if (updatedStatus == 0) {
                throw new BadRequestException("Slot Ini Telah Di Booking!");
            }

            String slotName = String.format("%s - %s",
                    trainerSlot.getTrainer().getName(),
                    trainerSlot.getDescription());

            Map<String, String> bookingDetail = new HashMap<>();
            bookingDetail.put("id", request.getSlotId());
            bookingDetail.put("name", slotName);
            bookingDetail.put("price", trainerSlot.getTrainer().getRate().toString());
            bookingDetail.put("quantity", "1");

            List<Map<String, String>> bookingDetails = new ArrayList<>();
            bookingDetails.add(bookingDetail);

            TransactionData transactionData = TransactionData.builder()
                    .packageId(trainerSlotId)
                    .packageName(slotName)
                    .grossAmount(trainerSlot.getTrainer().getRate())
                    .type(TransactionType.PT_BOOKING)
                    .itemDetails(bookingDetails)
                    .paymentType(request.getPaymentType())
                    .callbackUrl(ptBookedRedirectUrl)
                    .build();

            return transactionService.createTransaction(member, transactionData);
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
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

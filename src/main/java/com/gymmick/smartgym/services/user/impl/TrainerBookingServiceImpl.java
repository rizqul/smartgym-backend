package com.gymmick.smartgym.services.user.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.trainer.TrainerTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.TrainerBooking;
import com.gymmick.smartgym.entity.TrainerSlot;
import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.SlotStatus;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.repositories.TrainerBookingRepository;
import com.gymmick.smartgym.repositories.TrainerSlotRepository;
import com.gymmick.smartgym.repositories.TransactionRespository;
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
    if (member.getRole() != UserRole.MEMBER) {
      throw new BadCredentialsException("hanya member yang bisa melakukan booked trainer");
    }

    UUID slotId = UUID.fromString(request.getSlotId());

    try {
      Optional<Transaction> transaction = transactionRespository
          .findFirstByMemberIdAndStatusEqualsAndTypeOrderByCreatedAtAsc(member.getId(), TransactionStatus.PENDING,
              TransactionType.PT_BOOKING);

      transaction.ifPresent(t -> {
        if (t.getStatus().equals(TransactionStatus.PENDING)) {
          throw new BadRequestException("Silahkan Selesaikan Transaksi Sebelumnya!");
        }
      });

      TrainerSlot slot = trainerSlotRepository.findById(slotId).orElseThrow(() -> new ResourceNotFoundException());

      Map<String, String> bookingDetail = new HashMap<>();
      bookingDetail.put("id", request.getSlotId());
      bookingDetail.put("name", member.getName() + "-" + slot.getTrainer().getName());
      bookingDetail.put("price", slot.getTrainer().getRate().toString());
      bookingDetail.put("quantity", "1");

      List<Map<String, String>> bookingDetails = new ArrayList<>();
      bookingDetails.add(bookingDetail);

      TransactionData transactionData = TransactionData.builder()
          .packageId(slotId)
          .grossAmount(slot.getTrainer().getRate())
          .type(TransactionType.PT_BOOKING)
          .itemDetails(bookingDetails)
          .callbackUrl(ptBookedRedirectUrl)
          .build();

      slot.setStatus(SlotStatus.FULL_BOOKED);

      trainerSlotRepository.save(slot);

      String urlTransaction = transactionService.createTransaction(member, transactionData);

      return urlTransaction;
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  @Transactional
  public void finalizeBookedTrainer(Transaction transaction) {

    if (transaction.getStatus() == TransactionStatus.PROCESS) {
      TrainerSlot trainerSlot = trainerSlotRepository.findById(transaction.getPackageId())
          .orElseThrow(() -> new ResourceNotFoundException());

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

}

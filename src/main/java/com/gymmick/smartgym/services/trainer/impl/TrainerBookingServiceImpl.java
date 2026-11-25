package com.gymmick.smartgym.services.trainer.impl;

import java.time.Instant;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.pagination.PaginationMeta;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.trainer.TrainerBookingResponse;
import com.gymmick.smartgym.dto.trainer.TrainerBookingSearchRequest;
import com.gymmick.smartgym.dto.trainer.TrainerResponse;
import com.gymmick.smartgym.dto.trainer.TrainerTransactionCreateRequest;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.TrainerBookingMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.trainer.TrainerBookingService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.specification.TrainerBookingSpecification;
import com.gymmick.smartgym.specification.TrainerSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerBookingServiceImpl implements TrainerBookingService {

  private final TrainerSlotRepository trainerSlotRepository;
  private final TrainerBookingRepository trainerBookingRepository;
  private final TransactionRespository transactionRespository;

  private final TransactionService transactionService;

  private final TrainerBookingMapping trainerBookingMapping;

  @Value("${app.config.midtrans.pt-booking.redirect-url}")
  private String ptBookedRedirectUrl;

  @Override
  public PaginationResponse<TrainerBookingResponse> findAllBooking(TrainerBookingSearchRequest request) {

    Integer page = request.getPage();
    Integer size = request.getSize();

    Specification<TrainerBooking> specification = TrainerBookingSpecification
        .getAllTrainerBookingSpecification(request);

    Sort sortBy = Sort.by(TrainerBooking::getCreatedAt).descending();
    PageRequest pageRequest = PageRequest.of(page, size, sortBy);

    Page<TrainerBooking> trainerBookingPage = trainerBookingRepository.findAll(specification, pageRequest);
    List<TrainerBookingResponse> trainerBookings = trainerBookingMapping
        .toListResponse(trainerBookingPage.getContent());

    PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, trainerBookingPage.getTotalPages(), size,
        trainerBookingPage.getTotalElements(), p -> ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQueryParam("page", p)
            .toUriString());

    return PaginationResponse.<TrainerBookingResponse>builder()
        .data(trainerBookings)
        .pagination(paginationMeta)
        .build();
  }

  @Override
  public PaginationResponse<TrainerBookingResponse> findAllMyBooking(User memmber,
      TrainerBookingSearchRequest request) {

    Integer page = request.getPage();
    Integer size = request.getSize();

    Specification<TrainerBooking> specification = TrainerBookingSpecification
        .getMyTrainerBookingSpecification(memmber, request);

    Sort sortBy = Sort.by(TrainerBooking::getCreatedAt).descending();
    PageRequest pageRequest = PageRequest.of(page, size, sortBy);

    Page<TrainerBooking> trainerBookingPage = trainerBookingRepository.findAll(specification, pageRequest);
    List<TrainerBookingResponse> trainerBookings = trainerBookingMapping
        .toListResponse(trainerBookingPage.getContent());

    PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, trainerBookingPage.getTotalPages(), size,
        trainerBookingPage.getTotalElements(), p -> ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQueryParam("page", p)
            .toUriString());

    return PaginationResponse.<TrainerBookingResponse>builder()
        .data(trainerBookings)
        .pagination(paginationMeta)
        .build();
  }

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

      if (trainerSlot.getStatus().equals(SlotStatus.BOOKED)) {
        throw new BadRequestException("Slot Ini Telah Di Booking!");
      }

      trainerSlot.setStatus(SlotStatus.BOOKED);
      trainerSlot.setMember(member);

      trainerSlotRepository.save(trainerSlot);

      String slotName = String.format("%s [%s - %s]",
          trainerSlot.getTrainer().getName(),
          trainerSlot.getStartTime().toString(),
          trainerSlot.getEndTime().toString());

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

package com.gymmick.smartgym.services.trainer.impl;

import java.time.Instant;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.TrainerBookingMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.trainer.TrainerBookingService;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.specification.TrainerBookingSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                    throw new BadRequestException("Please complete previous transaction!");
                }
            });

            UUID trainerSlotId = UUID.fromString(request.getSlotId());

            TrainerSlot trainerSlot = trainerSlotRepository.findByIdForUpdate(trainerSlotId)
                    .orElseThrow(() -> new ResourceNotFoundException());

            if (Instant.now().isAfter(trainerSlot.getStartTime())) {
                throw new BadRequestException("Tidak dapat melakukan booking, waktu slot sudah terlewati!");
            }

            if (trainerSlot.getStatus().equals(SlotStatus.BOOKED)) {
<<<<<<< HEAD
                throw new BadRequestException("Slot Ini Telah Di Booking!");
=======
                throw new BadRequestException("This slot has been booked!");
>>>>>>> b9483e4 (feat: add update trainer slot and change some endpoint)
            }

            trainerSlot.setStatus(SlotStatus.BOOKED);
            trainerSlot.setMember(member);

            trainerSlotRepository.save(trainerSlot);

<<<<<<< HEAD
            String slotName = String.format("%s - %s",
                    trainerSlot.getTrainer().getName(),
                    trainerSlot.getDescription());

            Map<String, String> bookingDetail = new HashMap<>();
            bookingDetail.put("id", request.getSlotId());
            bookingDetail.put("name", slotName);
=======
            String slotName = String.format("%s [%s - %s]",
                    trainerSlot.getTrainer().getName(),
                    trainerSlot.getStartTime().toString(),
                    trainerSlot.getEndTime().toString());

            Map<String, String> bookingDetail = new HashMap<>();
            bookingDetail.put("id", request.getSlotId());
            bookingDetail.put("name", String.format("Trainer Booking - %s", trainerSlot.getTrainer().getName()));
>>>>>>> b9483e4 (feat: add update trainer slot and change some endpoint)
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

            log.info("Member {} booking trainer {}", member.getId(), trainerSlot.getTrainer().getName());

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
            TrainerSlot trainerSlot = this.findSlotById(transaction.getPackageId());

            TrainerBooking trainerBooking = TrainerBooking.builder()
                    .trainer(trainerSlot.getTrainer())
                    .member(transaction.getMember())
                    .slot(trainerSlot)
                    .transaction(transaction)
                    .build();

            trainerBookingRepository.save(trainerBooking);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRespository.save(transaction);

            log.info("Booking trainer slot ({}) successfully for member {}", trainerSlot.getId(),
                    transaction.getMember().getId());
        }
    }

    @Override
    @Transactional
    public void refundBookedTrainer(String packageId) {

        UUID trainerBookedId = UUID.fromString(packageId);
        TrainerBooking trainerBooking = this.findBookingById(trainerBookedId);

        Transaction transaction = trainerBooking.getTransaction();

        if (!transaction.getStatus().equals(TransactionStatus.SUCCESS)
                && !transaction.getType().equals(TransactionType.PT_BOOKING)) {

            throw new BadRequestException("This transaction cannot be refunded!");
        }

        String orderId = transaction.getOrderId();
        transactionService.refundTransaction(orderId);

        transaction.setStatus(TransactionStatus.REFUNDED);
        transactionRespository.save(transaction);

        TrainerSlot trainerSlot = this.findSlotById(trainerBooking.getSlot().getId());
        trainerSlot.setStatus(SlotStatus.AVAILABLE);

        trainerSlotRepository.save(trainerSlot);
    }

    public TrainerSlot findSlotById(UUID id) {
        return trainerSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    private TrainerBooking findBookingById(UUID id) {
        return trainerBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }
}

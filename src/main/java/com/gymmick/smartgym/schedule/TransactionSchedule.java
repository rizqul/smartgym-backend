package com.gymmick.smartgym.schedule;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.repositories.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionSchedule {

    private final TrainerSlotRepository trainerSlotRepository;
    private final TransactionRespository transactionRespository;

    @Transactional
    @Scheduled(fixedRate = 60_000)
    public void revertBackTrainerSlotWhenPaymentExpired() {
        transactionRespository.findByType(TransactionType.PT_BOOKING)
                .stream()
                .forEach(transaction -> {
                    if (transaction.getExpiredAt() != null
                            && Instant.now().isAfter(transaction.getExpiredAt())
                            && !transaction.getStatus().equals(TransactionStatus.SUCCESS)
                            && !transaction.getStatus().equals(TransactionStatus.EXPIRED)) {

                        trainerSlotRepository.updateStatusAndMemberIfMatch(transaction.getPackageId(),
                                null,
                                SlotStatus.BOOKED,
                                SlotStatus.AVAILABLE);

                        transaction.setStatus(TransactionStatus.EXPIRED);
                        transactionRespository.save(transaction);
                    }
                });
    }
}

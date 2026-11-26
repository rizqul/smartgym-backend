package com.gymmick.smartgym.repositories;

import java.time.Instant;
import java.util.*;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.gymmick.smartgym.entity.TrainerSlot;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.SlotStatus;

public interface TrainerSlotRepository extends JpaRepository<TrainerSlot, UUID>, JpaSpecificationExecutor<TrainerSlot> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TrainerSlot t WHERE t.id = :id")
    Optional<TrainerSlot> findByIdForUpdate(@Param("id") UUID id);

    @Query("""
                SELECT t FROM TrainerSlot t
                WHERE (t.trainer.id = :trainerId)
                    AND t.slotDate >= :startOfDay
                    AND t.slotDate < :endOfDay
                ORDER BY t.startTime ASC
            """)
    List<TrainerSlot> findAllByTrainerIdAndSlotDateBetween(
            @Param("trainerId") UUID trainerId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay);

    @Query("""
                SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
                FROM TrainerSlot t
                WHERE (t.trainer.id = :trainerId)
                    AND t.startTime < :endTime
                    AND t.endTime > :startTime
            """)
    boolean existsOverlappingSlot(
            @Param("trainerId") UUID trainerId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    @Query("""
                SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
                FROM TrainerSlot t
                WHERE (t.id <> :id)
                    AND t.trainer.id = :trainerId
                    AND t.startTime < :endTime
                    AND t.endTime > :startTime
            """)
    boolean existsOverlappingSlotOnAnotherSlotId(
            @Param("id") UUID id,
            @Param("trainerId") UUID trainerId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    @Query("""
            SELECT COUNT(DISTINCT s.trainer.id)
            FROM TrainerSlot s
            WHERE s.slotDate >= :startOfDay

              AND s.slotDate < :endOfDay
            """)
    Long countDistinctTrainersWithSlotsOnDate(
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay);

    @Query("""
            SELECT COUNT(*)
            FROM TrainerSlot s
            WHERE s.slotDate >= :startOfDay
              AND s.slotDate < :endOfDay
            """)
    Long countTrainerSlotOnDate(
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay);
}

package com.gymmick.smartgym.repositories;

import java.time.Instant;
import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.gymmick.smartgym.entity.TrainerSlot;

public interface TrainerSlotRepository extends JpaRepository<TrainerSlot, UUID>, JpaSpecificationExecutor<TrainerSlot> {

    @Query("""
        SELECT t FROM TrainerSlot t 
        WHERE (t.trainer.id = :trainerId OR t.trainer.user.id = :trainerId) 
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
        WHERE (t.trainer.id = :trainerId OR t.trainer.user.id = :trainerId) 
            AND t.startTime < :endTime 
            AND t.endTime > :startTime
    """)
    boolean existsOverlappingSlot(
            @Param("trainerId") UUID trainerId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);
}

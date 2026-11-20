package com.gymmick.smartgym.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.*;

import com.gymmick.smartgym.entity.TrainerBooking;

public interface TrainerBookingRepository
        extends JpaRepository<TrainerBooking, UUID>, JpaSpecificationExecutor<TrainerBooking> {

}

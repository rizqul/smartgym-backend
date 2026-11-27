package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.trainer.TrainerBookingResponse;
import com.gymmick.smartgym.entity.TrainerBooking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerBookingMapping {

    List<TrainerBookingResponse> toListResponse(List<TrainerBooking> trainerSlots);
}

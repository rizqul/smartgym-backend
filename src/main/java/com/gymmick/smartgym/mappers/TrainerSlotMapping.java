package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.trainer.TrainerSlotResponse;
import com.gymmick.smartgym.entity.TrainerSlot;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerSlotMapping {

    List<TrainerSlotResponse> toListResponse(List<TrainerSlot> trainerSlots);
}

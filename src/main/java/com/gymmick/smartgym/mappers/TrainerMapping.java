package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.*;

import com.gymmick.smartgym.dto.trainer.TrainerResponse;
import com.gymmick.smartgym.entity.Trainer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapping {

    List<TrainerResponse> toListResponses(List<Trainer> trainers);
}

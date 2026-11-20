package com.gymmick.smartgym.services.user;

import java.util.List;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.trainer.*;

public interface TrainerService {

  PaginationResponse<TrainerResponse> findAllTrainer(TrainerSearchRequest request);

  void createTrainer(TrainerCreateRequest request);

  void createSlot(String trainerId, TrainerSlotRequest request);

  List<TrainerSlotResponse> findSlotByTrainerIdAndDate(String id, TrainerSlotFilterRequest request);

}

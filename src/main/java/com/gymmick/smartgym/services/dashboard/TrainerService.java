package com.gymmick.smartgym.services.dashboard;

import java.util.List;

import com.gymmick.smartgym.dto.common.*;
import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;

public interface TrainerService {

    PaginationResponse<UserTrainerResponse> findAllTrainer(UserFilterRequest request);

    void createTrainer(TrainerCreateRequest request);

    void createSlot(String trainerId, TrainerSlotRequest request);

    List<TrainerSlotResponse> findSlotByTrainerIdAndDate(String id, TrainerSlotFilterRequest request);
}

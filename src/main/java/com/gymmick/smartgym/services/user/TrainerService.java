package com.gymmick.smartgym.services.user;

import java.util.List;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.trainer.*;
import com.gymmick.smartgym.dto.user.*;

public interface TrainerService {

    PaginationResponse<UserTrainerResponse> findAllTrainer(UserFilterRequest request);

    void createTrainer(TrainerCreateRequest request);

    void createSlot(String trainerId, TrainerSlotRequest request);

    List<TrainerSlotResponse> findSlotByTrainerIdAndDate(String id, TrainerSlotFilterRequest request);
}

package com.gymmick.smartgym.dto.trainer;

import java.util.UUID;

import com.gymmick.smartgym.dto.user.UserResponse;

import lombok.Data;

@Data
public class TrainerBookingResponse {

    private UUID id;

    private UserResponse member;

    private TrainerResponse trainer;

    private TrainerSlotResponse slot;
}

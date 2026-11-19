package com.gymmick.smartgym.dto.common;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.entity.enums.SlotStatus;

import lombok.*;

@Data
@Builder
public class TrainerSlotResponse {

    private UUID id;

    private String description;

    private Integer quota;

    private Instant startTime;

    private Instant endTime;

    private SlotStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}

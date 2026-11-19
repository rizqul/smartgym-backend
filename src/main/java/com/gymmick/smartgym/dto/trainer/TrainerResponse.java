package com.gymmick.smartgym.dto.trainer;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.entity.enums.*;

import lombok.*;

@Data
@Builder
public class TrainerResponse {

    private UUID id;

    private String name;

    private String profileUrl;

    private String phoneNumber;

    private TrainerSpecialization specialization;

    private Integer experience;

    private Long rate;

    private Instant createdAt;

    private Instant updatedAt;
}

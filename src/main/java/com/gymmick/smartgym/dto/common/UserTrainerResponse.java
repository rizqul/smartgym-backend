package com.gymmick.smartgym.dto.common;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.entity.enums.*;

import lombok.*;

@Data
@Builder
public class UserTrainerResponse {

    private UUID id;

    private UUID userId;

    private String name;

    private String email;

    private String profileUrl;

    private String phoneNumber;

    private TrainerSpecialization specialization;

    private Integer experience;

    private Long rate;

    private UserRole role;

    private Instant createdAt;

    private Instant updatedAt;
}

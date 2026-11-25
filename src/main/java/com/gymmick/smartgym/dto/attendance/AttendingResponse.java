package com.gymmick.smartgym.dto.attendance;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.dto.user.UserResponse;
import com.gymmick.smartgym.entity.enums.*;

import lombok.*;

@Data
@Builder
public class AttendingResponse {

    private UUID id;

    private UserResponse member;

    private AttendingMethod method;

    private AttendingStatus status;

    private Instant checkInAt;

    private Instant checkOutAt;

    private String denialReason;
}

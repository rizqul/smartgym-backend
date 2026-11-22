package com.gymmick.smartgym.dto.checkin;

import java.time.Instant;
import java.util.UUID;

import com.gymmick.smartgym.entity.enums.CheckinMethod;
import com.gymmick.smartgym.entity.enums.CheckinStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckinResponse {
    private UUID id;
    private String memberName;
    private UUID memberId;
    private Instant membershipExpiresAt;
    private CheckinMethod method;
    private CheckinStatus status;
    private Instant checkInAt;
    private String denialReason;
}

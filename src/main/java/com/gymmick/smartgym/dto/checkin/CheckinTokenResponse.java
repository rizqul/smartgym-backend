package com.gymmick.smartgym.dto.checkin;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckinTokenResponse {
    private String token;
    private Instant expiresAt;
    private Long expiresInSeconds;
}

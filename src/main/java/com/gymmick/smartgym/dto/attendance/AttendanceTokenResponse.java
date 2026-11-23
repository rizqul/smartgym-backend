package com.gymmick.smartgym.dto.attendance;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceTokenResponse {

    private String token;
    
    private Instant expiresAt;
    
    private Long expiresInSeconds;
}

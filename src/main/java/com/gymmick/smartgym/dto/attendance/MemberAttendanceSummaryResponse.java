package com.gymmick.smartgym.dto.attendance;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberAttendanceSummaryResponse {

    private long totalGymMinutes;

    private float totalGymHours;

    private long currentStreak;

    private long longestStreak;

    private long totalVisits;

    private Instant lastCheckInAt;

    private boolean checkedInToday;
}

package com.gymmick.smartgym.dto.attendance;

import lombok.*;

@Getter
@Builder
public class AttendanceHourlySlot {

    private String label;

    private long todayCount;

    private long lastWeekCount;
}

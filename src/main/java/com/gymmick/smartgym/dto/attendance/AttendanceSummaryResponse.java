package com.gymmick.smartgym.dto.attendance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceSummaryResponse {

    private long historyCount;

    private long checkinsToday;

    private long activeNow;
}

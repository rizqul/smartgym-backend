package com.gymmick.smartgym.dto.checkin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckinSummaryResponse {
    private long historyCount;
    private long checkinsToday;
    private long onFloor;
}

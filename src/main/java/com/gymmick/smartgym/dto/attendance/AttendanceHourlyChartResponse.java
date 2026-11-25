package com.gymmick.smartgym.dto.attendance;

import java.util.List;

import lombok.*;

@Getter
@Builder
public class AttendanceHourlyChartResponse {

    private String date;

    private List<AttendanceHourlySlot> slots;
}

package com.gymmick.smartgym.dto.statistic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardCountResponse {

    private Long allMember;

    private Long activeMember;

    private Long totalTrainer;

    private Long staffCount;

    private Long incomeYearly;
}

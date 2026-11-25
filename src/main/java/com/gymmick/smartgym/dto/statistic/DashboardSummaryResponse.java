package com.gymmick.smartgym.dto.statistic;

import java.util.List;

import com.gymmick.smartgym.dto.attendance.AttendingResponse;
import com.gymmick.smartgym.dto.transaction.TransactionResponse;

import lombok.*;

@Data
@Builder
public class DashboardSummaryResponse {

    List<AttendingResponse> attendanceResponse;

    List<TransactionResponse> transactionResponses;
}

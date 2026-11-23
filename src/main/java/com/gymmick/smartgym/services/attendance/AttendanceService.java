package com.gymmick.smartgym.services.attendance;

import java.util.*;

import com.gymmick.smartgym.dto.attendance.*;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.entity.User;

public interface AttendanceService {

    AttendanceSummaryResponse getSummary();

    PaginationResponse<AttendingResponse> findAllMyAttendance(User member, AttendanceFilterRequest request);

    AttendanceTokenResponse generateQrToken();

    AttendingResponse checkIn(String token, User member);

    AttendingResponse checkOut(UUID memberId);
}

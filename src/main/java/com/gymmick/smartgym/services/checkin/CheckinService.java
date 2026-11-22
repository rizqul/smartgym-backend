package com.gymmick.smartgym.services.checkin;

import java.util.List;
import java.util.UUID;

import com.gymmick.smartgym.dto.checkin.CheckinResponse;
import com.gymmick.smartgym.dto.checkin.CheckinSummaryResponse;
import com.gymmick.smartgym.dto.checkin.CheckinTokenResponse;
import com.gymmick.smartgym.entity.User;

public interface CheckinService {
    CheckinTokenResponse generateQrToken();
    CheckinResponse checkIn(String token, User member);
    CheckinResponse checkOut(UUID memberId);
    List<CheckinResponse> findRecentCheckins(int limit);
    CheckinSummaryResponse getSummary();
}

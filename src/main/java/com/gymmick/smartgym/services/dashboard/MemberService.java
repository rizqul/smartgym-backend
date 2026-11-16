package com.gymmick.smartgym.services.dashboard;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.exceptions.EmailSenderException;

public interface MemberService {

    PaginationResponse<UserResponse> findAllMember(UserFilterRequest request);

    void requestOtp(MemberOtpRequest request) throws EmailSenderException;

    void createMember(MemberCreateRequest request);

    void deleteMember(String id);
}

package com.gymmick.smartgym.services.user;

import com.gymmick.smartgym.dto.member.*;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.exceptions.EmailSenderException;

public interface MemberService {

    PaginationResponse<UserResponse> findAllMember(UserFilterRequest request);

    void requestOtp(MemberOtpRequest request) throws EmailSenderException;

    void createMember(MemberCreateRequest request);

    void deleteMember(String id);
}

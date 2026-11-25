package com.gymmick.smartgym.services.user;

import com.gymmick.smartgym.dto.member.*;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.exceptions.EmailSenderException;

public interface MemberService {

    PaginationResponse<UserResponse> findAllMember(UserSearchRequest request);

    void requestOtp(MemberOtpRequest request) throws EmailSenderException;

    void createMember(MemberCreateRequest request);

    void updateMember(String id, MemberUpdateRequest request);

    void deleteMember(String id);

    long countAllMember();

    long countAllActiveMember();

    long countAllInactiveMember();

    long countNewMember();
}

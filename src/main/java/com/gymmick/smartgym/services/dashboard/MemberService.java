package com.gymmick.smartgym.services.dashboard;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.UserFilterRequest;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;

public interface MemberService {

    PaginationResponse<UserResponse> findAllMember(UserFilterRequest request);
}

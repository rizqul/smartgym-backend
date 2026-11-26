package com.gymmick.smartgym.services.user;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.user.*;

public interface StaffService {

    Long countStaff();

    PaginationResponse<UserResponse> findAllStaff(UserSearchRequest request);

    void createStaff(UserStaffCreateRequest request);

    void updateStaff(String id, UserStaffUpdateRequest request);

    void deleteStaff(String id);
}

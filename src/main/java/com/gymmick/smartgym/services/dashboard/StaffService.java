package com.gymmick.smartgym.services.dashboard;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.StaffCreateRequest;
import com.gymmick.smartgym.dto.dashboard.StaffUpdateRequest;
import com.gymmick.smartgym.dto.dashboard.UserFilterRequest;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;

public interface StaffService {

  void createStaff(StaffCreateRequest request);

  void deleteStaff(String id);

  PaginationResponse<UserResponse> findAllStaff(UserFilterRequest request);

  void updateStaff(String id, StaffUpdateRequest request);
}

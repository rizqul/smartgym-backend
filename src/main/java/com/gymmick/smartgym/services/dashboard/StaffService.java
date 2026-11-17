package com.gymmick.smartgym.services.dashboard;

import com.gymmick.smartgym.dto.dashboard.StaffCreateRequest;

public interface StaffService {

  void createStaff(StaffCreateRequest request);

  void deleteStaff(String id);
}

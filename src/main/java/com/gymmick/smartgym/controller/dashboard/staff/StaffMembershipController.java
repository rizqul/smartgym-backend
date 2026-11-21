package com.gymmick.smartgym.controller.dashboard.staff;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.membership.MembershipPackageResponse;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.services.membership.MembershipService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff/dashboard/memberships")
@RequiredArgsConstructor
public class StaffMembershipController {

  private final MembershipService membershipService;

  @GetMapping(path = "/packages")
  public ResponseEntity<ApiResponse<Map<MembershipType, List<MembershipPackageResponse>>>> findAllMemberPackage() {
    Map<MembershipType, List<MembershipPackageResponse>> responses = membershipService.findAllPackage();

    return ApiResponse.success(HttpStatus.OK, "OK", responses);
  }

}

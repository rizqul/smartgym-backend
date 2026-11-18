package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.StaffCreateRequest;
import com.gymmick.smartgym.dto.dashboard.StaffUpdateRequest;
import com.gymmick.smartgym.dto.dashboard.UserFilterRequest;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.services.dashboard.StaffService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/staff")
@RequiredArgsConstructor
public class AdminStaffController {

  private final StaffService staffService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<UserResponse>>> search(UserFilterRequest request) {
    PaginationResponse<UserResponse> response = staffService.findAllStaff(request);

    return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
  }

  @PostMapping(path = "/create")
  public ResponseEntity<ApiResponse<UserResponse>> createStaff(@Valid @RequestBody StaffCreateRequest request) {

    staffService.createStaff(request);

    String message = "staff berhasil dibuat";
    return ApiResponse.success(HttpStatus.CREATED, message);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable String id) {

    staffService.deleteStaff(id);

    String message = "Berhasil Menghapus staff!";
    return ApiResponse.success(HttpStatus.OK, message);
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<ApiResponse<String>> updateStaff(@PathVariable String id,
      @Valid @RequestBody StaffUpdateRequest request) {

    staffService.updateStaff(id, request);

    String message = "Berhasil Mengupdate staff";

    return ApiResponse.success(HttpStatus.OK, message);
  }

}

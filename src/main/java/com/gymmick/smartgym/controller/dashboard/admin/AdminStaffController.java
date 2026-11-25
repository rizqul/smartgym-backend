package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.statistic.StaffCountResponse;
import com.gymmick.smartgym.dto.user.*;
import com.gymmick.smartgym.services.user.StaffService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> search(UserSearchRequest request) {
        PaginationResponse<UserResponse> response = staffService.findAllStaff(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @PostMapping(path = "/create")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(@Valid @RequestBody UserStaffCreateRequest request) {

        staffService.createStaff(request);

        String message = "staff berhasil dibuat";
        return ApiResponse.success(HttpStatus.CREATED, message);
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateStaff(@PathVariable String id,
            @Valid @ModelAttribute UserStaffUpdateRequest request) {

        staffService.updateStaff(id, request);

        String message = "Berhasil Mengupdate staff";

        return ApiResponse.success(HttpStatus.OK, message);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteStaff(@PathVariable String id) {

        staffService.deleteStaff(id);

        String message = "Berhasil Menghapus staff!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<ApiResponse<StaffCountResponse>> countStaff() {
        Long staffCount = staffService.countStaff();

        StaffCountResponse response = StaffCountResponse.builder()
                .staffCount(staffCount)
                .build();
        return ApiResponse.success(HttpStatus.OK, response);
    }
}

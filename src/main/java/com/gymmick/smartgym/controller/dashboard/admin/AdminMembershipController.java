package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.services.dashboard.MembershipService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/memberships")
@RequiredArgsConstructor
public class AdminMembershipController {

    private final MembershipService membershipService;

    @GetMapping(path = "/packages")
    public ResponseEntity<ApiResponse<Map<MembershipType, List<MembershipPackageResponse>>>> findAllMemberPackage() {
        Map<MembershipType, List<MembershipPackageResponse>> responses = membershipService.findAllPackage();

        return ApiResponse.success(HttpStatus.OK, "OK", responses);
    }

    @PostMapping(path = "/packages/create")
    public ResponseEntity<ApiResponse<String>> createMemberPackage(
            @Valid @RequestBody MembershipPackageCreateRequest request) {

        membershipService.createPackage(request);

        String message = "Berhasil Menambahkan Membership Package!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @DeleteMapping(path = "/packages/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMemberPackage(@PathVariable String id) {

        membershipService.deletePackage(id);

        String message = "Berhasil Menghapus Membership Package!";
        return ApiResponse.success(HttpStatus.OK, message);
    }
}

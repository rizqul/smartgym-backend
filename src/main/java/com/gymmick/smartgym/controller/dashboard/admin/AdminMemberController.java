package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.UserFilterRequest;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.services.dashboard.MemberService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> search(UserFilterRequest request) {
        PaginationResponse<UserResponse> response = memberService.findAllMember(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }
}

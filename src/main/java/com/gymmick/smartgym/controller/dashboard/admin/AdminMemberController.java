package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.dto.dashboard.*;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.services.dashboard.MemberService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
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

  @PostMapping(path = "/otp")
  public ResponseEntity<ApiResponse<String>> requestOtp(@Valid @RequestBody MemberOtpRequest request) {

    memberService.requestOtp(request);

    String message = "Berhasil Mengirim Kode OTP!";
    return ApiResponse.success(HttpStatus.OK, message);
  }

  @PostMapping(path = "/create")
  public ResponseEntity<ApiResponse<String>> createMember(@Valid @RequestBody MemberCreateRequest request) {

    memberService.createMember(request);

    String message = "Berhasil Menambahkan Member!";
    return ApiResponse.success(HttpStatus.CREATED, message);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable String id) {

    memberService.deleteMember(id);

    String message = "Berhasil Menghapus Member!";
    return ApiResponse.success(HttpStatus.OK, message);
  }
}

package com.gymmick.smartgym.controller.dashboard.staff;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymmick.smartgym.dto.member.MemberCreateRequest;
import com.gymmick.smartgym.dto.member.MemberOtpRequest;
import com.gymmick.smartgym.dto.member.MemberUpdateRequest;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.statistic.MemberCountResponse;
import com.gymmick.smartgym.dto.user.UserSearchRequest;
import com.gymmick.smartgym.dto.user.UserResponse;
import com.gymmick.smartgym.services.user.MemberService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/staff/dashboard/member")
@RequiredArgsConstructor
public class StaffMemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> search(UserSearchRequest request) {
        PaginationResponse<UserResponse> response = memberService.findAllMember(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @PostMapping(path = "/otp")
    public ResponseEntity<ApiResponse<String>> requestOtp(@Valid @RequestBody MemberOtpRequest request) {

        memberService.requestOtp(request);

        String message = "Berhasil mengirim kode OTP!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<ApiResponse<String>> createMember(@Valid @RequestBody MemberCreateRequest request) {

        memberService.createMember(request);

        String message = "Berhasil menambahkan member!";
        return ApiResponse.success(HttpStatus.CREATED, message);
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateMember(@PathVariable String id,
            @Valid @ModelAttribute MemberUpdateRequest request) {

        memberService.updateMember(id, request);

        String message = "Berhasil memperbarui data member!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable String id) {

        memberService.deleteMember(id);

        String message = "Berhasil menghapus member!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<ApiResponse<MemberCountResponse>> countAllMember() {
        Long allMember = memberService.countAllMember();
        Long activeMember = memberService.countAllActiveMember();
        Long inactiveMember = memberService.countAllInactiveMember();
        Long newMember = memberService.countNewMember();

        MemberCountResponse response = MemberCountResponse.builder()
                .memberCount(allMember)
                .memberActiveCount(activeMember)
                .memberInactiveCount(inactiveMember)
                .newMemberCount(newMember)
                .build();

        return ApiResponse.success(HttpStatus.OK, response);
    }
}

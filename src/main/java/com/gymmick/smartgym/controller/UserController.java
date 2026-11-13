package com.gymmick.smartgym.controller;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapping userMapping;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> profile(@AuthenticationPrincipal User user) {

        UserResponse userResponse = userMapping.toResponse(user);

        return ApiResponse.success(HttpStatus.OK, userResponse);
    }
}

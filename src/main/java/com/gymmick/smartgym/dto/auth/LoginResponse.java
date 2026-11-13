package com.gymmick.smartgym.dto.auth;

import com.gymmick.smartgym.dto.common.UserResponse;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginResponse {

    private UserResponse user;

    private String accessToken;

    private String refreshToken;
}

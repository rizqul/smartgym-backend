package com.gymmick.smartgym.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
public class RefreshTokenResponse {

    private String accessToken;

    private String refreshToken;
}
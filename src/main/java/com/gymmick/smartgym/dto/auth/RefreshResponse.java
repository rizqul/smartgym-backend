package com.gymmick.smartgym.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
public class RefreshResponse {

    private String accessToken;

    private String refreshToken;
}
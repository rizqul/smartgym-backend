package com.gymmick.smartgym.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
public class ResetPasswordOTPResponse {

    private String resetToken;
}

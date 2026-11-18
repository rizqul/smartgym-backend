package com.gymmick.smartgym.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ResetPasswordOTPRequest {

    @NotBlank(message = "The code field is required.")
    private String code;
}

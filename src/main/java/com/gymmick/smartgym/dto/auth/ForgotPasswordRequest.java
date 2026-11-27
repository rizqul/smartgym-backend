package com.gymmick.smartgym.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ForgotPasswordRequest {

    @NotBlank(message = "The email field is required.")
    @Email(message = "The email field must be a valid email address.")
    private String email;
}

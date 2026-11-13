package com.gymmick.smartgym.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "The email field is required.")
    @Email(message = "The email field must be a valid email address.")
    private String email;

    @NotBlank(message = "The code field is required.")
    private String code;

    @NotBlank(message = "The password field is required.")
    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String password;
}

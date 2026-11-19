package com.gymmick.smartgym.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
public class UserUpdatePasswordRequest {

    @NotBlank(message = "The email field is required.")
    @Email(message = "The email field must be a valid email address.")
    private String email;

    @NotBlank
    private String oldPassword;

    @NotBlank(message = "The password field is required.")
    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String newPassword;
}

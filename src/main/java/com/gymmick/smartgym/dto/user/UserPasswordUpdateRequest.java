package com.gymmick.smartgym.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
public class UserPasswordUpdateRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank(message = "The password field is required.")
    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String newPassword;
}

package com.gymmick.smartgym.dto.dashboard;

import com.gymmick.smartgym.validation.annotation.*;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class MemberCreateRequest {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The email field is required.")
    @Email(message = "The email field must be a valid email address.")
    private String email;

    @NotBlank(message = "The code field is required.")
    @Size(min = 6, max = 6, message = "The code field must be exact 6 numbers.")
    private String code;

    @NotBlank(message = "The phone number field is required.")
    @IdPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "The password field is required.")
    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String password;

    public String getName() {
        return this.name.trim();
    }

    public String getEmail() {
        return this.email.trim().toLowerCase();
    }
}

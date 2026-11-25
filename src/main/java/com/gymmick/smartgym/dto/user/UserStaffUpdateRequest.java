package com.gymmick.smartgym.dto.user;

import com.gymmick.smartgym.validation.annotation.*;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class UserStaffUpdateRequest {

    private String name;

    @Email(message = "The email field must be a valid email address.")
    private String email;

    @IdPhoneNumber
    private String phoneNumber;

    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String password;

    @File(contentType = { "image/png", "image/jpg",
            "image/jpeg" }, message = "File photo harus berupa png, jpg, atau jpeg!", required = false)
    @FileSize(max = 2, message = "Ukuran file photo tidak boleh lebih dari 2MB!", required = false)
    private Object photo;
}

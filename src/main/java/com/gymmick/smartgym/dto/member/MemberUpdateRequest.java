package com.gymmick.smartgym.dto.member;

import com.gymmick.smartgym.validation.annotation.*;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class MemberUpdateRequest {

    private String name;

    @Email(message = "The email field must be a valid email address.")
    private String email;

    @IdPhoneNumber
    private String phoneNumber;

    @File(contentType = { "image/png", "image/jpg",
            "image/jpeg" }, message = "Photo files must be png, jpg, or jpeg!", required = false)
    @FileSize(max = 2, message = "The photo file size cannot be more than 2MB!", required = false)
    private Object photo;

    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String password;
}

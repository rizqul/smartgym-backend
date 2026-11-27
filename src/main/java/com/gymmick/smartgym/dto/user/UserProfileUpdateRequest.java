package com.gymmick.smartgym.dto.user;

import com.gymmick.smartgym.validation.annotation.*;

import lombok.*;

@Getter
@Setter
public class UserProfileUpdateRequest {

    private String name;

    @IdPhoneNumber
    private String phoneNumber;

    @File(contentType = { "image/png", "image/jpg",
            "image/jpeg" }, message = "Photo files must be png, jpg, or jpeg!", required = false)
    @FileSize(max = 2, message = "The photo file size cannot be more than 2MB!", required = false)
    private Object photo;
}

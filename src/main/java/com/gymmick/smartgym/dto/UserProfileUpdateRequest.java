package com.gymmick.smartgym.dto;

import com.gymmick.smartgym.validation.annotation.*;

import lombok.*;

@Getter
@Setter
public class UserProfileUpdateRequest {

    private String name;

    @IdPhoneNumber
    private String phoneNumber;

    @File(contentType = { "image/png", "image/jpg",
            "image/jpeg" }, message = "File photo harus berupa png, jpg, atau jpeg!", required = false)
    @FileSize(max = 2, message = "Ukuran file photo tidak boleh lebih dari 2MB!", required = false)
    private Object photo;
}

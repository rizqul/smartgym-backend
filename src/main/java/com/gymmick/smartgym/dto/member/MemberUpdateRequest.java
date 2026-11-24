package com.gymmick.smartgym.dto.member;

import com.gymmick.smartgym.validation.annotation.*;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class MemberUpdateRequest {

    private String name;

    private String email;

    @IdPhoneNumber
    private String phoneNumber;

    @File(contentType = { "image/png", "image/jpg",
            "image/jpeg" }, message = "File photo harus berupa png, jpg, atau jpeg!", required = false)
    @FileSize(max = 2, message = "Ukuran file photo tidak boleh lebih dari 2MB!", required = false)
    private Object photo;

    @Size(min = 8, message = "The password field must be at least 8 characters.")
    private String password;
}

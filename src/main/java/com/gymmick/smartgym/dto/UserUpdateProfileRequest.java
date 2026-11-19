
package com.gymmick.smartgym.dto;

import com.gymmick.smartgym.validation.annotation.*;

import lombok.*;

@Getter
@Setter
public class UserUpdateProfileRequest {

    private String name;

    @IdPhoneNumber
    private String phoneNumber;

    private String profileUrl;
}

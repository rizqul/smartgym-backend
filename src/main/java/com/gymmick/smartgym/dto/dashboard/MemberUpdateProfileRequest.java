
package com.gymmick.smartgym.dto.dashboard;

import com.gymmick.smartgym.validation.annotation.*;

import lombok.*;

@Getter
@Setter
public class MemberUpdateProfileRequest {

    private String name;

    @IdPhoneNumber
    private String phoneNumber;

    private String profileUrl;
}

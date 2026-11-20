
package com.gymmick.smartgym.dto.member;

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

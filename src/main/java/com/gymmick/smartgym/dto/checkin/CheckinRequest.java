package com.gymmick.smartgym.dto.checkin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckinRequest {
    @NotBlank
    private String token;
}

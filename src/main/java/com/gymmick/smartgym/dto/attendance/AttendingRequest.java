package com.gymmick.smartgym.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class AttendingRequest {

    @NotBlank
    private String token;
}

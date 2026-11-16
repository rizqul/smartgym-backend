package com.gymmick.smartgym.dto.dashboard;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class MembershipPackageCreateRequest {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotNull(message = "The duration field is required.")
    private Integer duration;

    @NotNull(message = "The price field is required.")
    private Long price;
}

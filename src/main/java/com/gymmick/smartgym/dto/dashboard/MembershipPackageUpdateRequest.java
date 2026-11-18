package com.gymmick.smartgym.dto.dashboard;

import com.gymmick.smartgym.entity.enums.MembershipType;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class MembershipPackageUpdateRequest {

    @NotBlank(message = "The type field is required.")
    private String type;

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The description field is required.")
    private String description;

    @NotNull(message = "The duration field is required.")
    private Integer duration;

    public MembershipType getType() {
        return MembershipType.isValid(this.type)
                ? MembershipType.valueOf(this.type.toUpperCase())
                : MembershipType.MONTHLY;
    }
}

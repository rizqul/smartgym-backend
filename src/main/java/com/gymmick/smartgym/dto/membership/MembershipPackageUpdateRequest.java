package com.gymmick.smartgym.dto.membership;

import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.validation.annotation.PositiveNumber;

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
    @PositiveNumber(min = 1, message = "The duration field must be a number and greater than 0.")
    private String duration;

    public MembershipType getType() {
        return MembershipType.isValid(this.type)
                ? MembershipType.valueOf(this.type.toUpperCase())
                : MembershipType.MONTHLY;
    }

    public Integer getDuration() {
        return Integer.parseInt(this.duration);
    }
}

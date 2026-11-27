package com.gymmick.smartgym.dto.equipment;

import com.gymmick.smartgym.entity.enums.EquipmentStatus;
import com.gymmick.smartgym.validation.annotation.EnumValue;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCreateRequest {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The status field is required.")
    @EnumValue(enumClass = EquipmentStatus.class, message = "These provided equipment status was invalid.")
    private String status;

    private String profileUrl;

    public EquipmentStatus getEquipmentStatus() {
        return EquipmentStatus.valueOf(this.status.trim().toUpperCase());
    }
}

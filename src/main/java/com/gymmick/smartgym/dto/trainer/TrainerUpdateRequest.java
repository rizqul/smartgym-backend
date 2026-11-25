package com.gymmick.smartgym.dto.trainer;

import java.util.UUID;

import com.gymmick.smartgym.entity.enums.TrainerSpecialization;
import com.gymmick.smartgym.validation.annotation.EnumValue;
import com.gymmick.smartgym.validation.annotation.File;
import com.gymmick.smartgym.validation.annotation.FileSize;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrainerUpdateRequest {

    private UUID id;

    private String name;

    private String email;

    private String profileUrl;

    private String phoneNumber;

    @EnumValue(enumClass = TrainerSpecialization.class, message = "These provided equipment status was invalid.")
    private String specialization;

    private Integer experience;

    private Long rate;

    @File(contentType = { "image/png", "image/jpg",
            "image/jpeg" }, message = "File photo harus berupa png, jpg, atau jpeg!", required = false)
    @FileSize(max = 2, message = "Ukuran file photo tidak boleh lebih dari 2MB!", required = false)
    private Object photo;

    public TrainerSpecialization getSpecialization() {
        return TrainerSpecialization.valueOf(this.specialization.trim().toUpperCase());
    }
}

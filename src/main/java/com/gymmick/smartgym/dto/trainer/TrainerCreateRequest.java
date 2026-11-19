package com.gymmick.smartgym.dto.trainer;

import com.gymmick.smartgym.entity.enums.TrainerSpecialization;
import com.gymmick.smartgym.validation.annotation.*;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class TrainerCreateRequest {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The phone number field is required.")
    @IdPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "The specialization field is required.")
    @EnumValue(enumClass = TrainerSpecialization.class, message = "These provided specialization was invalid.")
    private String specialization;

    @NotBlank(message = "The experience field is required.")
    @NumberValue(message = "The experience field must be a number.")
    private String experience;

    @NotBlank(message = "The rate field is required.")
    @NumberValue(message = "The rate field must be a number.")
    private String rate;

    public TrainerSpecialization getSpecialization() {
        return TrainerSpecialization.valueOf(this.specialization.trim().toUpperCase());
    }

    public Integer getExperience() {
        return Integer.parseInt(this.experience);
    }

    public Long getRate() {
        return Long.parseLong(this.rate);
    }
}

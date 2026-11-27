package com.gymmick.smartgym.validation;

import java.util.Arrays;

import com.gymmick.smartgym.validation.annotation.EnumValue;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidation implements ConstraintValidator<EnumValue, String> {

    private Enum<?>[] constants;

    @Override
    public void initialize(EnumValue annotation) {
        this.constants = annotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return Arrays.stream(constants).anyMatch(v -> v.name().equalsIgnoreCase(value.trim().toUpperCase()));
    }
}

package com.gymmick.smartgym.validation;

import com.gymmick.smartgym.validation.annotation.PositiveNumber;

import jakarta.validation.*;

public class PositiveNumberValidation implements ConstraintValidator<PositiveNumber, String> {

    private long minValue;

    @Override
    public void initialize(PositiveNumber constraintAnnotation) {
        this.minValue = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return value.matches("^[1-9]\\d*$") && Long.parseLong(value) >= minValue;
    }
}

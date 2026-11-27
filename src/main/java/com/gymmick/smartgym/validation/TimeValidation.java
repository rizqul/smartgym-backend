package com.gymmick.smartgym.validation;

import com.gymmick.smartgym.validation.annotation.Time;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeValidation implements ConstraintValidator<Time, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return value.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }
}

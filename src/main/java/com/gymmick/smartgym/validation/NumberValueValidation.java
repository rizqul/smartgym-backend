package com.gymmick.smartgym.validation;

import com.gymmick.smartgym.validation.annotation.NumberValue;

import jakarta.validation.*;

public class NumberValueValidation implements ConstraintValidator<NumberValue, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return value.matches("^\\d+$");
    }
}

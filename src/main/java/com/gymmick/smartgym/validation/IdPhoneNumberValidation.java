package com.gymmick.smartgym.validation;

import com.gymmick.smartgym.validation.annotation.IdPhoneNumber;

import jakarta.validation.*;

public class IdPhoneNumberValidation implements ConstraintValidator<IdPhoneNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return value.matches("^(62|0)8([0-9]){8,11}$");
    }
}

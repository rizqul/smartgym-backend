package com.gymmick.smartgym.validation;

import java.time.*;

import com.gymmick.smartgym.validation.annotation.FutureOrPresentDateTime;

import jakarta.validation.*;

public class FutureOrPresentDateTimeValidation implements ConstraintValidator<FutureOrPresentDateTime, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        if (!value.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])T([01]\\d|2[0-3]):[0-5]\\d$")) {
            return true;
        }

        Instant input = LocalDateTime.parse(value).atZone(ZoneId.systemDefault()).toInstant();

        return input.isAfter(Instant.now());
    }
}

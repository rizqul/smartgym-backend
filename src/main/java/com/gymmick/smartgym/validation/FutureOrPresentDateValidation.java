package com.gymmick.smartgym.validation;

import java.time.*;
import java.time.temporal.ChronoUnit;

import com.gymmick.smartgym.validation.annotation.FutureOrPresentDate;

import jakarta.validation.*;

public class FutureOrPresentDateValidation implements ConstraintValidator<FutureOrPresentDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        if (!value.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
            return true;
        }

        Instant input = LocalDate.parse(value).atStartOfDay().atZone(ZoneId.systemDefault())
                .toInstant().plus(Duration.ofDays(1)).truncatedTo(ChronoUnit.DAYS);

        return LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().isBefore(input);
    }
}

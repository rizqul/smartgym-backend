package com.gymmick.smartgym.validation.annotation;

import java.lang.annotation.*;

import com.gymmick.smartgym.validation.DateTimeValidation;

import jakarta.validation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidation.class)
public @interface DateTime {

    String message() default "The input field must be a date time.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

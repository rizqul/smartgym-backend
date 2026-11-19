package com.gymmick.smartgym.validation.annotation;

import java.lang.annotation.*;

import com.gymmick.smartgym.validation.DateValidation;

import jakarta.validation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidation.class)
public @interface Date {

    String message() default "The input field must be a date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

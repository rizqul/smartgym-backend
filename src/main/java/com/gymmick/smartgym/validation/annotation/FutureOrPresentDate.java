package com.gymmick.smartgym.validation.annotation;

import java.lang.annotation.*;

import com.gymmick.smartgym.validation.FutureOrPresentDateValidation;

import jakarta.validation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureOrPresentDateValidation.class)
public @interface FutureOrPresentDate {

    String message() default "The input field must be in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.gymmick.smartgym.validation.annotation;

import jakarta.validation.*;
import java.lang.annotation.*;

import com.gymmick.smartgym.validation.PositiveNumberValidation;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveNumberValidation.class)

public @interface PositiveNumber {
    String message() default "The input must be a positive number.";

    long min() default 0;

    boolean required() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

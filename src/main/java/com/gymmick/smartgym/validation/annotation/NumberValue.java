package com.gymmick.smartgym.validation.annotation;

import java.lang.annotation.*;

import com.gymmick.smartgym.validation.NumberValueValidation;

import jakarta.validation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberValueValidation.class)
public @interface NumberValue {

    String message() default "The input field must be a number.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

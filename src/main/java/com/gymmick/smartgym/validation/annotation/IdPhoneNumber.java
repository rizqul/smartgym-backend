package com.gymmick.smartgym.validation.annotation;

import java.lang.annotation.*;

import com.gymmick.smartgym.validation.IdPhoneNumberValidation;

import jakarta.validation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdPhoneNumberValidation.class)
public @interface IdPhoneNumber {

    String message() default "These provided phone number was invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.gymmick.smartgym.validation.annotation;

import java.lang.annotation.*;

import com.gymmick.smartgym.validation.FileValidation;

import jakarta.validation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidation.class)
public @interface File {

    String message() default "The input field must be a valid file.";

    boolean required() default true;

    String[] contentType() default { "*/*" };

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

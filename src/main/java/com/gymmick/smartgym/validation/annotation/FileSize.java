package com.gymmick.smartgym.validation.annotation;

import jakarta.validation.*;
import java.lang.annotation.*;

import com.gymmick.smartgym.validation.FileSizeValidation;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidation.class)

public @interface FileSize {
    String message() default "The file must not be greater than 10MB.";

    long max() default 10;

    boolean required() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

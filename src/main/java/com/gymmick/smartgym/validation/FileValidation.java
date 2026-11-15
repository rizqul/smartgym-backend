package com.gymmick.smartgym.validation;

import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import com.gymmick.smartgym.validation.annotation.File;

import jakarta.validation.*;

public class FileValidation implements ConstraintValidator<File, Object> {

    private String[] contentType;

    @Override
    public void initialize(File annotation) {
        this.contentType = annotation.contentType();
    }

    @Override
    public boolean isValid(Object file, ConstraintValidatorContext context) {

        if (file == null || file.getClass().equals(String.class)) {
            return false;
        }

        if (!(file instanceof MultipartFile uploadedFile) || uploadedFile.isEmpty()) {
            return false;
        }

        String fileContentType = uploadedFile.getContentType();

        return Arrays.stream(contentType)
                .anyMatch(type -> type.equals("*/*") || type.equalsIgnoreCase(fileContentType));
    }
}

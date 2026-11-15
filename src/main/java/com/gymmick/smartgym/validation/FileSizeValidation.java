package com.gymmick.smartgym.validation;

import org.springframework.web.multipart.MultipartFile;

import com.gymmick.smartgym.validation.annotation.FileSize;

import jakarta.validation.*;

public class FileSizeValidation implements ConstraintValidator<FileSize, Object> {

    private double maxSize;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object file, ConstraintValidatorContext context) {

        if (file == null || file.getClass().equals(String.class)) {
            return false;
        }

        if (!(file instanceof MultipartFile uploadedFile) || uploadedFile.isEmpty()) {
            return false;
        }

        return uploadedFile.getSize() <= maxSize * 1024 * 1024;
    }
}

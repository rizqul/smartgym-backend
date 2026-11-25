package com.gymmick.smartgym.validation;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import com.gymmick.smartgym.validation.annotation.File;

import jakarta.validation.*;

public class FileValidation implements ConstraintValidator<File, Object> {

    private static final Tika tika = new Tika();

    private Boolean required;
    private String[] contentType;

    @Override
    public void initialize(File annotation) {
        this.required = annotation.required();
        this.contentType = annotation.contentType();
    }

    @Override
    public boolean isValid(Object file, ConstraintValidatorContext context) {

        if (!this.required && file == null) {
            return true;
        }

        if (file == null || file.getClass().equals(String.class)) {
            return false;
        }

        if (!(file instanceof MultipartFile uploadedFile) || uploadedFile.isEmpty()) {
            return false;
        }

        String mimeType;

        try (InputStream inputStream = uploadedFile.getInputStream()) {
            mimeType = tika.detect(inputStream);
        } catch (Exception e) {
            return false;
        }

        return Arrays.stream(contentType)
                .anyMatch(type -> type.equals("*/*") || type.equalsIgnoreCase(mimeType));
    }
}

package com.gymmick.smartgym.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Data Tidak Ditemukan!");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

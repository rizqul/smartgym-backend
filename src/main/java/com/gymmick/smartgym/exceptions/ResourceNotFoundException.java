package com.gymmick.smartgym.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("The resource you are trying to access doesn't exist!");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

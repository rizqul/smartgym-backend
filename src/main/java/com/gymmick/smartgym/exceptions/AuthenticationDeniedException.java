package com.gymmick.smartgym.exceptions;

public class AuthenticationDeniedException extends RuntimeException {

    public AuthenticationDeniedException() {
        super("Silahkan Login Terlebih Dahulu!");
    }

    public AuthenticationDeniedException(String message) {
        super(message);
    }
}

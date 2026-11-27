package com.gymmick.smartgym.exceptions;

public class AuthenticationDeniedException extends RuntimeException {

    public AuthenticationDeniedException() {
        super("Unauthenticated");
    }

    public AuthenticationDeniedException(String message) {
        super(message);
    }
}

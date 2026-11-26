package com.gymmick.smartgym.exceptions;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.utils.ApiResponse;

@RestControllerAdvice
public class GlobalException {

    // Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleArgumentNotValidException(
            MethodArgumentNotValidException exception) {

        ObjectError objectError = exception.getBindingResult().getAllErrors().getFirst();

        String message = objectError.getDefaultMessage();
        return ApiResponse.error(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }

    // Authentication
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequestException(
            BadRequestException exception) {

        String message = exception.getMessage();
        return ApiResponse.error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentialsException(
            BadCredentialsException exception) {

        String message = "Incorrect email or password!";
        return ApiResponse.error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameNotFoundException(
            UsernameNotFoundException exception) {

        String message = exception.getMessage();
        return ApiResponse.error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AuthenticationDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationDeniedException(
            AuthenticationDeniedException exception) {

        String message = exception.getMessage();
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationDeniedException(
            AuthorizationDeniedException exception) {

        String message = exception.getMessage();
        return ApiResponse.error(HttpStatus.FORBIDDEN, message);
    }

    // Global Exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(
            RuntimeException exception) {

        String message = exception.getMessage();
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(
            Exception exception) {

        String message = exception.getMessage();
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}

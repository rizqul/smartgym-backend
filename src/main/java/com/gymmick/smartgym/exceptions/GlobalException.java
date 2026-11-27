package com.gymmick.smartgym.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.utils.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    // Database
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception) {

        String message = "Can't inserted duplicate data!";
        return ApiResponse.error(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }

    // Global Exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        String message = exception.getMessage();

        log.info("Argument error: {}", message);

        if (message.contains("UUID")) {
            message = "The resource you are trying to access doesn't exist!";

            return ApiResponse.error(HttpStatus.NOT_FOUND, message);
        }

        return ApiResponse.error(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(
            ResourceNotFoundException exception) {

        String message = exception.getMessage();

        log.info("Resource not found: {}", message);

        return ApiResponse.error(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(
            RuntimeException exception) {

        log.info("Internal server error. Error: {}", exception.getMessage());

        String message = "Internal server error";
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(
            Exception exception) {

        log.info("Internal server error. Error: {}", exception.getMessage());

        String message = "Internal server error";
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}

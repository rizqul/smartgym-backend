package com.gymmick.smartgym.services.auth;

import org.springframework.security.authentication.BadCredentialsException;

import com.gymmick.smartgym.dto.auth.*;
import com.gymmick.smartgym.exceptions.*;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request)
            throws BadRequestException, BadCredentialsException;

    void forgotPassword(ForgotPasswordRequest request)
            throws EmailSenderException;

    void resetPassword(ResetPasswordRequest request)
            throws EmailSenderException;

    RefreshResponse refresh(HttpServletRequest request)
            throws BadRequestException;

    void logout(HttpServletRequest request)
            throws AuthenticationDeniedException, BadRequestException;
}

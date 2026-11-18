package com.gymmick.smartgym.services.auth;

import com.gymmick.smartgym.dto.auth.*;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    ResetPasswordOTPResponse resetOtpValidation(ResetPasswordOTPRequest request);

    void resetPassword(HttpServletRequest httpRequest, ResetPasswordRequest request);

    RefreshTokenResponse refreshToken(HttpServletRequest request);

    void logout(HttpServletRequest request);
}

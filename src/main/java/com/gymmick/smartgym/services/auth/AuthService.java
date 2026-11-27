package com.gymmick.smartgym.services.auth;

import com.gymmick.smartgym.dto.auth.*;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    ResetPasswordOTPResponse resetOtpValidation(ResetPasswordOTPRequest request);

    void resetPassword(String token, ResetPasswordRequest request);

    RefreshTokenResponse refreshToken(String token);

    void logout(String token);
}

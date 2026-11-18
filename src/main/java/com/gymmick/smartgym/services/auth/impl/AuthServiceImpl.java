package com.gymmick.smartgym.services.auth.impl;

import java.time.*;
import java.util.*;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import com.gymmick.smartgym.dto.auth.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.services.auth.*;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.services.email.EmailService;
import com.gymmick.smartgym.utils.*;

import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final OtpService otpService;

    private final JwtToken jwtToken;

    private final UserMapping userMapping;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = (User) userService.loadUserByUsername(request.getEmail());

        UUID accessTokenId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        tokenService.store(accessTokenId, refreshTokenId, user, TokenType.AUTH_USER);

        String accessToken = jwtToken.createToken(accessTokenId, Duration.ofHours(1));
        String refreshToken = jwtToken.createToken(refreshTokenId, Duration.ofDays(30));

        LoginResponse loginResponse = LoginResponse.builder()
                .user(userMapping.toResponse(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return loginResponse;
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        if (!userService.existsByEmail(request.getEmail())) {
            return;
        }

        String otpCode = OtpToken.generateOtp();

        otpService.store(request.getEmail(), otpCode, OtpType.RESET_PASSWORD, Duration.ofMinutes(10));

        String emailTemplate = "email/auth/reset-password";
        String emailSubject = "Reset Password";

        Map<String, Object> variables = new HashMap<>();
        variables.put("email", request.getEmail());
        variables.put("code", otpCode);

        emailService.sendHtmlEmail(emailTemplate, emailSubject, request.getEmail(), variables);
    }

    @Override
    @Transactional
    public ResetPasswordOTPResponse resetOtpValidation(ResetPasswordOTPRequest request) {

        Otp otp = otpService.findByCodeAndType(request.getCode(), OtpType.RESET_PASSWORD);

        if (otp.getExpiredAt().isBefore(Instant.now())) {
            throw new BadRequestException("Kode OTP Tidak Valid!");
        }

        otp.setExpiredAt(Instant.now());
        otpService.save(otp);

        User user = userService.findByEmail(otp.getEmail());

        UUID resetTokenId = UUID.randomUUID();
        tokenService.store(resetTokenId, user, TokenType.AUTH_RESET);

        String resetToken = jwtToken.createToken(resetTokenId, Duration.ofMinutes(10));

        return ResetPasswordOTPResponse.builder()
                .resetToken(resetToken)
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(HttpServletRequest httpRequest, ResetPasswordRequest request) {

        Cookie resetTokenCookie = WebUtils.getCookie(httpRequest, "reset-token");

        if (resetTokenCookie == null) {
            throw new BadRequestException("Terjadi Kesalahan, Silahkan Coba Lagi!");
        }

        UUID tokenId = jwtToken.getTokenId(resetTokenCookie.getValue());
        Token token = tokenService.findById(tokenId);

        if (!token.getRevoked()) {
            token.setRevoked(true);
            tokenService.save(token);

            User user = token.getUser();
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userService.save(user);
        }
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(HttpServletRequest request) {

        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh-token");

        if (refreshTokenCookie == null) {
            throw new AuthenticationDeniedException();
        }

        UUID tokenId = jwtToken.getTokenId(refreshTokenCookie.getValue());
        Token token = tokenService.findByRefreshId(tokenId);

        if (token != null && !token.getRevoked()) {
            token.setRevoked(true);
            tokenService.save(token);

            UUID accessTokenId = UUID.randomUUID();
            UUID refreshTokenId = UUID.randomUUID();

            tokenService.store(accessTokenId, refreshTokenId, token.getUser(), TokenType.AUTH_USER);

            String accessToken = jwtToken.createToken(accessTokenId, Duration.ofHours(1));
            String refreshToken = jwtToken.createToken(refreshTokenId, Duration.ofDays(30));

            RefreshTokenResponse refreshResponse = RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return refreshResponse;
        }

        throw new BadRequestException("These JWT token was invalid.");
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request)
            throws AuthenticationDeniedException, BadRequestException {

        Cookie accessToken = WebUtils.getCookie(request, "access-token");

        if (accessToken == null || accessToken.getValue().isEmpty()) {
            return;
        }

        UUID tokenId = jwtToken.getTokenId(accessToken.getValue());
        Token token = tokenService.findById(tokenId);

        if (token != null && !token.getRevoked()) {
            token.setRevoked(true);
            tokenService.save(token);
        }
    }
}

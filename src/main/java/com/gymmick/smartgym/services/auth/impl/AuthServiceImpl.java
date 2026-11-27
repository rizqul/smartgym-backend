package com.gymmick.smartgym.services.auth.impl;

import java.time.*;
import java.util.*;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.auth.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.auth.*;
import com.gymmick.smartgym.services.user.UserService;
import com.gymmick.smartgym.services.utils.EmailService;
import com.gymmick.smartgym.utils.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

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

        Optional<User> user = userRepository.findByEmailAndEnabledIsTrue(request.getEmail());

        if (!user.isPresent()) {
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
    public void resetPassword(String token, ResetPasswordRequest request) {

        UUID tokenId = jwtToken.getTokenId(token);
        Token resetToken = tokenService.findById(tokenId);

        if (!resetToken.getRevoked()) {
            resetToken.setRevoked(true);
            tokenService.save(resetToken);

            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(String token) {

        UUID tokenId = jwtToken.getTokenId(token);
        Token refreshToken = tokenService.findByRefreshId(tokenId);

        if (refreshToken.getRevoked()) {
            throw new BadRequestException("These JWT token is invalid or expired!");
        }

        refreshToken.setRevoked(true);
        tokenService.save(refreshToken);

        UUID accessTokenId = UUID.randomUUID();
        UUID refreshTokenId = UUID.randomUUID();

        tokenService.store(accessTokenId, refreshTokenId, refreshToken.getUser(), TokenType.AUTH_USER);

        String newAccessToken = jwtToken.createToken(accessTokenId, Duration.ofHours(1));
        String newRefreshToken = jwtToken.createToken(refreshTokenId, Duration.ofDays(30));

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }

    @Override
    @Transactional
    public void logout(String token) {

        if (token.isBlank()) {
            return;
        }

        UUID tokenId = jwtToken.getTokenId(token);
        Token authToken = tokenService.findById(tokenId);

        if (!authToken.getRevoked()) {
            authToken.setRevoked(true);
            tokenService.save(authToken);
        }
    }
}

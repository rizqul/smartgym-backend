package com.gymmick.smartgym.services.auth.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import com.gymmick.smartgym.dto.auth.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.OtpType;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.services.auth.*;
import com.gymmick.smartgym.services.common.UserService;
import com.gymmick.smartgym.services.email.EmailService;
import com.gymmick.smartgym.utils.JwtToken;
import com.gymmick.smartgym.utils.OtpToken;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
  public LoginResponse login(LoginRequest request)
      throws BadRequestException, BadCredentialsException {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User user = (User) userService.loadUserByUsername(request.getEmail());

    UUID accessTokenId = UUID.randomUUID();
    UUID refreshTokenId = UUID.randomUUID();

    tokenService.store(accessTokenId, refreshTokenId, user);

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
  public void forgotPassword(ForgotPasswordRequest request)
      throws EmailSenderException {

    userService.findByEmail(request.getEmail());

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
  public void resetPassword(ResetPasswordRequest request)
      throws EmailSenderException {

    Otp otp = otpService.findByEmailAndCode(request.getEmail(), request.getCode());

    if (!otp.getType().equals(OtpType.RESET_PASSWORD) || otp.getExpiredAt().isBefore(Instant.now())) {
      throw new BadRequestException("Kode Otp Tidak Valid!");
    }

    otp.setExpiredAt(Instant.now());
    otpService.update(otp);

    User user = userService.findByEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    userService.save(user);
  }

  @Override
  @Transactional
  public RefreshResponse refresh(HttpServletRequest request)
      throws BadRequestException {

    Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh-token");

    if (refreshTokenCookie == null) {
      throw new AuthenticationDeniedException();
    }

    UUID tokenId = jwtToken.getTokenId(refreshTokenCookie.getValue());
    Token token = tokenService.findByRefreshId(tokenId);

    if (token != null && !token.getRevoked()) {
      token.setRevoked(true);
      tokenService.update(token);

      UUID accessTokenId = UUID.randomUUID();
      UUID refreshTokenId = UUID.randomUUID();

      tokenService.store(accessTokenId, refreshTokenId, token.getUser());

      String accessToken = jwtToken.createToken(accessTokenId, Duration.ofHours(1));
      String refreshToken = jwtToken.createToken(refreshTokenId, Duration.ofDays(30));

      RefreshResponse refreshResponse = RefreshResponse.builder()
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
      tokenService.update(token);
    }
  }
}

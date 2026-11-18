package com.gymmick.smartgym.controller.auth;

import java.time.Duration;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import com.gymmick.smartgym.dto.auth.*;
import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.services.auth.AuthService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    public final AuthService authService;

    @Value("${app.config.cookie-domain}")
    private String cookieDomain;

    @Value("${app.config.cookie-same-site}")
    private String cookieSameSite;

    @Value("${app.config.cookie-secure}")
    private Boolean cookieSecure;

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie("access-token", response.getAccessToken(), Duration.ofHours(1)));
        cookies.add(createCookie("refresh-token", response.getRefreshToken(), Duration.ofDays(30)));

        String message = "Login Berhasil!";
        return ApiResponse.success(HttpStatus.OK, message, response.getUser(), cookies);
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgot(@Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        String message = "Kode OTP Berhasil Dikirim!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @PostMapping(path = "/reset-verify")
    public ResponseEntity<ApiResponse<String>> verify(@Valid @RequestBody ResetPasswordOTPRequest request) {

        ResetPasswordOTPResponse response = authService.resetOtpValidation(request);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie("reset-token", response.getResetToken(), Duration.ofMinutes(1)));

        return ApiResponse.success(HttpStatus.NO_CONTENT, cookies);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<ApiResponse<String>> reset(HttpServletRequest httpRequest,
            @Valid @RequestBody ResetPasswordRequest request) {

        String token = getCookieValue(httpRequest, "reset-token");

        authService.resetPassword(token, request);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie("reset-token", "", Duration.ofSeconds(0)));

        String message = "Berhasil Memperbaharui Kata Sandi!";
        return ApiResponse.success(HttpStatus.OK, message, cookies);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(HttpServletRequest request) {

        String token = getCookieValue(request, "refresh-token");

        RefreshTokenResponse response = authService.refreshToken(token);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie("access-token", response.getAccessToken(), Duration.ofHours(1)));
        cookies.add(createCookie("refresh-token", response.getRefreshToken(), Duration.ofDays(30)));

        return ApiResponse.success(HttpStatus.NO_CONTENT, cookies);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {

        String token = getCookieValue(request, "access-token");

        authService.logout(token);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie("access-token", "", Duration.ofSeconds(0)));
        cookies.add(createCookie("refresh-token", "", Duration.ofSeconds(0)));

        return ApiResponse.success(HttpStatus.NO_CONTENT, cookies);
    }

    private String createCookie(String name, String value, Duration duration) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(duration.toSeconds())
                .sameSite(cookieSameSite)
                .domain(cookieDomain)
                .secure(cookieSecure)
                .build()
                .toString();
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        return Optional.ofNullable(WebUtils.getCookie(request, name))
                .map(Cookie::getValue)
                .orElse("");
    }
}

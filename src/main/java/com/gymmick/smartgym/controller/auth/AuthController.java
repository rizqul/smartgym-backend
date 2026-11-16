package com.gymmick.smartgym.controller.auth;

import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.auth.*;
import com.gymmick.smartgym.dto.common.UserResponse;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.services.auth.AuthService;
import com.gymmick.smartgym.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    public final AuthService authService;

    @Value("${app.config.cookie-domain}")
    private String cookieDomain;

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        LoginResponse response = authService.login(loginRequest);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie(request, "access-token", response.getAccessToken(),
                Duration.ofHours(1)));
        cookies.add(createCookie(request, "refresh-token", response.getRefreshToken(),
                Duration.ofDays(30)));

        String message = "Login Berhasil!";
        return ApiResponse.success(HttpStatus.OK, message, response.getUser(), cookies);
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgot(
            @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        authService.forgotPassword(forgotPasswordRequest);

        String message = "Kode Otp Berhasil Dikirim!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<ApiResponse<String>> reset(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        authService.resetPassword(resetPasswordRequest);

        String message = "Berhasil Memperbaharui Kata Sandi!";
        return ApiResponse.success(HttpStatus.OK, message);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<ApiResponse<String>> refresh(HttpServletRequest request) {

        RefreshResponse response = authService.refresh(request);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie(request, "access-token", response.getAccessToken(),
                Duration.ofHours(1)));
        cookies.add(createCookie(request, "refresh-token", response.getRefreshToken(),
                Duration.ofDays(30)));

        return ApiResponse.success(HttpStatus.NO_CONTENT, cookies);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal User user, HttpServletRequest request) {

        authService.logout(request);

        List<String> cookies = new ArrayList<>();
        cookies.add(createCookie(request, "access-token", "", Duration.ofSeconds(0)));
        cookies.add(createCookie(request, "refresh-token", "", Duration.ofSeconds(0)));

        return ApiResponse.success(HttpStatus.NO_CONTENT, cookies);
    }

    private String createCookie(HttpServletRequest request, String name, String value, Duration duration) {

        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(duration.toSeconds())
                .sameSite("lax")
                .domain(cookieDomain)
                .secure(request.isSecure())
                .build()
                .toString();
    }
}

package com.gymmick.smartgym.services.auth.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gymmick.smartgym.dto.auth.ForgotPasswordRequest;
import com.gymmick.smartgym.dto.auth.LoginRequest;
import com.gymmick.smartgym.dto.auth.LoginResponse;
import com.gymmick.smartgym.dto.auth.RefreshTokenResponse;
import com.gymmick.smartgym.dto.auth.ResetPasswordOTPRequest;
import com.gymmick.smartgym.dto.auth.ResetPasswordOTPResponse;
import com.gymmick.smartgym.dto.auth.ResetPasswordRequest;
import com.gymmick.smartgym.dto.user.UserResponse;
import com.gymmick.smartgym.entity.Otp;
import com.gymmick.smartgym.entity.Token;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.OtpType;
import com.gymmick.smartgym.entity.enums.TokenType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.services.auth.OtpService;
import com.gymmick.smartgym.services.auth.TokenService;
import com.gymmick.smartgym.services.user.UserService;
import com.gymmick.smartgym.services.utils.EmailService;
import com.gymmick.smartgym.utils.JwtToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private TokenService tokenService;

    @Mock
    private OtpService otpService;

    @Mock
    private JwtToken jwtToken;

    @Mock
    private UserMapping userMapping;

    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> variablesCaptor;

    // ------------------------------------------------------------------
    // login
    // ------------------------------------------------------------------

    @Test
    @DisplayName("login: authenticates, stores tokens and returns access/refresh tokens with mapped user")
    void login_shouldAuthenticateStoreTokensAndReturnResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@gym.com");
        request.setPassword("password123");

        User user = User.builder().email(request.getEmail()).build();
        UserResponse userResponse = UserResponse.builder().email(request.getEmail()).build();

        when(userService.loadUserByUsername(request.getEmail())).thenReturn(user);
        when(jwtToken.createToken(any(UUID.class), eq(Duration.ofHours(1)))).thenReturn("access-token");
        when(jwtToken.createToken(any(UUID.class), eq(Duration.ofDays(30)))).thenReturn("refresh-token");
        when(userMapping.toResponse(user)).thenReturn(userResponse);

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getUser()).isSameAs(userResponse);
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo(request.getEmail());
        assertThat(authCaptor.getValue().getCredentials()).isEqualTo(request.getPassword());

        verify(tokenService).store(uuidCaptor.capture(), uuidCaptor.capture(), eq(user),
                eq(TokenType.AUTH_USER));
        List<UUID> storedIds = uuidCaptor.getAllValues();
        assertThat(storedIds).hasSize(2);
        assertThat(storedIds.get(0)).isNotNull();
        assertThat(storedIds.get(1)).isNotNull();
        assertThat(storedIds.get(0)).isNotEqualTo(storedIds.get(1));

        verify(jwtToken).createToken(eq(storedIds.get(0)), eq(Duration.ofHours(1)));
        verify(jwtToken).createToken(eq(storedIds.get(1)), eq(Duration.ofDays(30)));
        verify(userMapping).toResponse(user);
    }

    @Test
    @DisplayName("login: propagates authentication failure and does not issue tokens")
    void login_whenAuthenticationFails_shouldPropagateExceptionAndNotIssueTokens() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@gym.com");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(userService, never()).loadUserByUsername(anyString());
        verifyNoInteractions(tokenService, jwtToken, userMapping);
    }

    // ------------------------------------------------------------------
    // forgotPassword
    // ------------------------------------------------------------------

    @Test
    @DisplayName("forgotPassword: generates/stores OTP and sends reset-password email when email exists")
    void forgotPassword_whenEmailExists_shouldStoreOtpAndSendEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("user@gym.com");
        when(userService.existsByEmail(request.getEmail())).thenReturn(true);

        authService.forgotPassword(request);

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(otpService).store(eq(request.getEmail()), codeCaptor.capture(),
                eq(OtpType.RESET_PASSWORD), eq(Duration.ofMinutes(10)));
        assertThat(codeCaptor.getValue()).hasSize(6).containsOnlyDigits();

        verify(emailService).sendHtmlEmail(eq("email/auth/reset-password"), eq("Reset Password"),
                eq(request.getEmail()), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue())
                .containsEntry("email", request.getEmail())
                .containsEntry("code", codeCaptor.getValue());
    }

    @Test
    @DisplayName("forgotPassword: does nothing when email is not registered")
    void forgotPassword_whenEmailDoesNotExist_shouldDoNothing() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("unknown@gym.com");
        when(userService.existsByEmail(request.getEmail())).thenReturn(false);

        authService.forgotPassword(request);

        verify(userService).existsByEmail(request.getEmail());
        verifyNoInteractions(otpService, emailService);
    }

    // ------------------------------------------------------------------
    // resetOtpValidation
    // ------------------------------------------------------------------

    @Test
    @DisplayName("resetOtpValidation: expires OTP, issues reset token for the OTP owner")
    void resetOtpValidation_whenOtpIsValid_shouldExpireOtpAndReturnResetToken() {
        ResetPasswordOTPRequest request = new ResetPasswordOTPRequest();
        request.setCode("123456");
        String email = "user@gym.com";

        Otp otp = Otp.builder()
                .email(email)
                .code(request.getCode())
                .type(OtpType.RESET_PASSWORD)
                .expiredAt(Instant.now().plus(Duration.ofMinutes(5)))
                .build();
        User user = User.builder().email(email).build();

        when(otpService.findByCodeAndType(request.getCode(), OtpType.RESET_PASSWORD)).thenReturn(otp);
        when(userService.findByEmail(email)).thenReturn(user);
        when(jwtToken.createToken(any(UUID.class), eq(Duration.ofMinutes(10)))).thenReturn("reset-token");

        Instant before = Instant.now();
        ResetPasswordOTPResponse response = authService.resetOtpValidation(request);
        Instant after = Instant.now();

        assertThat(response.getResetToken()).isEqualTo("reset-token");
        assertThat(otp.getExpiredAt()).isBetween(before, after);
        verify(otpService).save(otp);
        verify(userService).findByEmail(email);
        verify(tokenService).store(uuidCaptor.capture(), eq(user), eq(TokenType.AUTH_RESET));
        assertThat(uuidCaptor.getValue()).isNotNull();
        verify(jwtToken).createToken(eq(uuidCaptor.getValue()), eq(Duration.ofMinutes(10)));
    }

    @Test
    @DisplayName("resetOtpValidation: rejects an expired OTP and stores nothing")
    void resetOtpValidation_whenOtpIsExpired_shouldThrowBadRequest() {
        ResetPasswordOTPRequest request = new ResetPasswordOTPRequest();
        request.setCode("123456");

        Otp otp = Otp.builder()
                .email("user@gym.com")
                .code(request.getCode())
                .type(OtpType.RESET_PASSWORD)
                .expiredAt(Instant.now().minus(Duration.ofMinutes(1)))
                .build();
        when(otpService.findByCodeAndType(request.getCode(), OtpType.RESET_PASSWORD)).thenReturn(otp);

        assertThatThrownBy(() -> authService.resetOtpValidation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Kode OTP Tidak Valid!");

        verify(otpService, never()).save(any());
        verifyNoInteractions(tokenService, userService, jwtToken);
    }

    // ------------------------------------------------------------------
    // resetPassword
    // ------------------------------------------------------------------

    @Test
    @DisplayName("resetPassword: revokes token and saves newly encoded password")
    void resetPassword_whenTokenIsValid_shouldRevokeTokenAndUpdatePassword() {
        String token = "reset-jwt";
        UUID tokenId = UUID.randomUUID();
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("newPassword123");

        User user = User.builder().email("user@gym.com").password("old-password").build();
        Token resetToken = Token.builder()
                .id(tokenId)
                .type(TokenType.AUTH_RESET)
                .user(user)
                .revoked(false)
                .build();

        when(jwtToken.getTokenId(token)).thenReturn(tokenId);
        when(tokenService.findById(tokenId)).thenReturn(resetToken);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");

        authService.resetPassword(token, request);

        assertThat(resetToken.getRevoked()).isTrue();
        verify(tokenService).save(resetToken);
        verify(passwordEncoder).encode(request.getPassword());
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        verify(userService).save(user);
    }

    @Test
    @DisplayName("resetPassword: does nothing when the reset token is already revoked")
    void resetPassword_whenTokenAlreadyRevoked_shouldDoNothing() {
        String token = "reset-jwt";
        UUID tokenId = UUID.randomUUID();
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("newPassword123");

        User user = User.builder().email("user@gym.com").password("old-password").build();
        Token resetToken = Token.builder().id(tokenId).user(user).revoked(true).build();

        when(jwtToken.getTokenId(token)).thenReturn(tokenId);
        when(tokenService.findById(tokenId)).thenReturn(resetToken);

        authService.resetPassword(token, request);

        verify(tokenService, never()).save(any());
        verify(userService, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    // ------------------------------------------------------------------
    // refreshToken
    // ------------------------------------------------------------------

    @Test
    @DisplayName("refreshToken: revokes old token and issues a new token pair")
    void refreshToken_whenTokenIsValid_shouldRevokeOldTokenAndReturnNewTokens() {
        String token = "refresh-jwt";
        UUID tokenId = UUID.randomUUID();
        User user = User.builder().email("user@gym.com").build();
        Token refreshToken = Token.builder()
                .id(tokenId)
                .type(TokenType.AUTH_USER)
                .user(user)
                .revoked(false)
                .build();

        when(jwtToken.getTokenId(token)).thenReturn(tokenId);
        when(tokenService.findByRefreshId(tokenId)).thenReturn(refreshToken);
        when(jwtToken.createToken(any(UUID.class), eq(Duration.ofHours(1)))).thenReturn("new-access");
        when(jwtToken.createToken(any(UUID.class), eq(Duration.ofDays(30)))).thenReturn("new-refresh");

        RefreshTokenResponse response = authService.refreshToken(token);

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(refreshToken.getRevoked()).isTrue();
        verify(tokenService).save(refreshToken);

        verify(tokenService).store(uuidCaptor.capture(), uuidCaptor.capture(), eq(user),
                eq(TokenType.AUTH_USER));
        List<UUID> storedIds = uuidCaptor.getAllValues();
        assertThat(storedIds).hasSize(2);
        assertThat(storedIds.get(0)).isNotEqualTo(storedIds.get(1));
        verify(jwtToken).createToken(eq(storedIds.get(0)), eq(Duration.ofHours(1)));
        verify(jwtToken).createToken(eq(storedIds.get(1)), eq(Duration.ofDays(30)));
    }

    @Test
    @DisplayName("refreshToken: rejects a revoked refresh token")
    void refreshToken_whenTokenIsRevoked_shouldThrowBadRequest() {
        String token = "refresh-jwt";
        UUID tokenId = UUID.randomUUID();
        Token refreshToken = Token.builder().id(tokenId).revoked(true).build();

        when(jwtToken.getTokenId(token)).thenReturn(tokenId);
        when(tokenService.findByRefreshId(tokenId)).thenReturn(refreshToken);

        assertThatThrownBy(() -> authService.refreshToken(token))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Token JWT tidak valid atau telah kedaluwarsa!");

        verify(tokenService, never()).save(any());
        verify(tokenService, never()).store(any(), any(), any(), any());
        verify(jwtToken, never()).createToken(any(), any());
    }

    // ------------------------------------------------------------------
    // logout
    // ------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "   " })
    @DisplayName("logout: ignores a blank token")
    void logout_whenTokenIsBlank_shouldDoNothing(String token) {
        authService.logout(token);

        verifyNoInteractions(jwtToken, tokenService);
    }

    @Test
    @DisplayName("logout: revokes and saves a valid auth token")
    void logout_whenTokenIsValid_shouldRevokeToken() {
        String token = "auth-jwt";
        UUID tokenId = UUID.randomUUID();
        Token authToken = Token.builder().id(tokenId).revoked(false).build();

        when(jwtToken.getTokenId(token)).thenReturn(tokenId);
        when(tokenService.findById(tokenId)).thenReturn(authToken);

        authService.logout(token);

        assertThat(authToken.getRevoked()).isTrue();
        verify(tokenService).save(authToken);
    }

    @Test
    @DisplayName("logout: does not save an already revoked token")
    void logout_whenTokenAlreadyRevoked_shouldNotSaveAgain() {
        String token = "auth-jwt";
        UUID tokenId = UUID.randomUUID();
        Token authToken = Token.builder().id(tokenId).revoked(true).build();

        when(jwtToken.getTokenId(token)).thenReturn(tokenId);
        when(tokenService.findById(tokenId)).thenReturn(authToken);

        authService.logout(token);

        verify(tokenService, never()).save(any());
    }
}

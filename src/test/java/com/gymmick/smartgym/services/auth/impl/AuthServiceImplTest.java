
package com.gymmick.smartgym.services.auth.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gymmick.smartgym.dto.auth.LoginRequest;
import com.gymmick.smartgym.dto.auth.LoginResponse;
import com.gymmick.smartgym.dto.auth.RefreshTokenResponse;
import com.gymmick.smartgym.entity.Token;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.TokenType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.repositories.UserRepository;
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
  private UserRepository userRepository;

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

  private User user;

  @BeforeEach
  void setUp() {
    user = mock(User.class);
  }

  // 1. Successful login
  @Test
  void login_shouldReturnTokens_whenCredentialsAreValid() {

    // Arrange
    LoginRequest request = new LoginRequest();
    request.setEmail("member@example.com");
    request.setPassword("password123");

    when(userService.loadUserByUsername(request.getEmail()))
        .thenReturn(user);

    when(jwtToken.createToken(any(UUID.class), eq(Duration.ofHours(1))))
        .thenReturn("access-token");

    when(jwtToken.createToken(any(UUID.class), eq(Duration.ofDays(30))))
        .thenReturn("refresh-token");

    // Act
    LoginResponse response = authService.login(request);

    // Assert
    assertNotNull(response);
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());

    verify(authenticationManager).authenticate(any(Authentication.class));

    verify(tokenService).store(
        any(UUID.class),
        any(UUID.class),
        eq(user),
        eq(TokenType.AUTH_USER));

    verify(userMapping).toResponse(user);
  }

  // 2. Failed login
  @Test
  void login_shouldThrowException_whenCredentialsAreInvalid() {

    // Arrange
    LoginRequest request = new LoginRequest();
    request.setEmail("member@example.com");
    request.setPassword("wrongpassword");

    doThrow(new BadCredentialsException("Bad credentials"))
        .when(authenticationManager)
        .authenticate(any(Authentication.class));

    // Act & Assert
    assertThrows(
        BadCredentialsException.class,
        () -> authService.login(request));

    // No tokens should be created or stored.
    verifyNoInteractions(tokenService);
    verifyNoInteractions(jwtToken);
  }

  // 3. Successful refresh token
  @Test
  void refreshToken_shouldGenerateNewTokens_whenRefreshTokenIsValid() {

    // Arrange
    String oldRefreshToken = "old-refresh-token";
    UUID tokenId = UUID.randomUUID();

    Token storedToken = mock(Token.class);

    when(jwtToken.getTokenId(oldRefreshToken))
        .thenReturn(tokenId);

    when(tokenService.findByRefreshId(tokenId))
        .thenReturn(storedToken);

    when(storedToken.getRevoked())
        .thenReturn(false);

    when(storedToken.getUser())
        .thenReturn(user);

    when(jwtToken.createToken(any(UUID.class), eq(Duration.ofHours(1))))
        .thenReturn("new-access-token");

    when(jwtToken.createToken(any(UUID.class), eq(Duration.ofDays(30))))
        .thenReturn("new-refresh-token");

    // Act
    RefreshTokenResponse response = authService.refreshToken(oldRefreshToken);

    // Assert
    assertNotNull(response);
    assertEquals("new-access-token", response.getAccessToken());
    assertEquals("new-refresh-token", response.getRefreshToken());

    verify(storedToken).setRevoked(true);
    verify(tokenService).save(storedToken);

    verify(tokenService).store(
        any(UUID.class),
        any(UUID.class),
        eq(user),
        eq(TokenType.AUTH_USER));
  }

  // 4. Refresh token already revoked
  @Test
  void refreshToken_shouldThrowException_whenTokenIsRevoked() {

    // Arrange
    String oldRefreshToken = "revoked-refresh-token";
    UUID tokenId = UUID.randomUUID();

    Token storedToken = mock(Token.class);

    when(jwtToken.getTokenId(oldRefreshToken))
        .thenReturn(tokenId);

    when(tokenService.findByRefreshId(tokenId))
        .thenReturn(storedToken);

    when(storedToken.getRevoked())
        .thenReturn(true);

    // Act & Assert
    assertThrows(
        BadRequestException.class,
        () -> authService.refreshToken(oldRefreshToken));

    verify(tokenService, never()).store(
        any(UUID.class),
        any(UUID.class),
        any(User.class),
        any(TokenType.class));
  }

  // 5. Successful logout
  @Test
  void logout_shouldRevokeToken_whenTokenIsValid() {

    // Arrange
    String accessToken = "valid-access-token";
    UUID tokenId = UUID.randomUUID();

    Token storedToken = mock(Token.class);

    when(jwtToken.getTokenId(accessToken))
        .thenReturn(tokenId);

    when(tokenService.findById(tokenId))
        .thenReturn(storedToken);

    when(storedToken.getRevoked())
        .thenReturn(false);

    // Act
    authService.logout(accessToken);

    // Assert
    verify(storedToken).setRevoked(true);
    verify(tokenService).save(storedToken);
  }

  // 6. Logout with blank token
  @Test
  void logout_shouldDoNothing_whenTokenIsBlank() {

    // Act
    authService.logout("");

    // Assert
    verifyNoInteractions(jwtToken);
    verifyNoInteractions(tokenService);
  }
}

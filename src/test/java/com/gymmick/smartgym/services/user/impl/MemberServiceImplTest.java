
package com.gymmick.smartgym.services.user.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gymmick.smartgym.dto.member.*;
import com.gymmick.smartgym.entity.Otp;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.OtpType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.mappers.UserMapping;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.auth.OtpService;
import com.gymmick.smartgym.services.user.UserService;
import com.gymmick.smartgym.services.utils.EmailService;
import com.gymmick.smartgym.services.utils.StorageService;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserService userService;
  @Mock
  private OtpService otpService;
  @Mock
  private EmailService emailService;
  @Mock
  private StorageService storageService;
  @Mock
  private UserMapping userMapping;
  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private MemberServiceImpl memberService;

  private UUID memberId;

  @BeforeEach
  void setUp() {
    memberId = UUID.randomUUID();
  }

  @Test
  void countAllMember_shouldReturnRepositoryCount() {
    when(userRepository.countByRoleAndEnabledIsTrue(any()))
        .thenReturn(20L);

    long result = memberService.countAllMember();

    assertEquals(20L, result);
  }

  @Test
  void countAllActiveMember_shouldReturnRepositoryCount() {
    when(userRepository.countByRoleAndMembershipExpiredAtAfterAndEnabledIsTrue(
        any(), any(Instant.class)))
        .thenReturn(12L);

    long result = memberService.countAllActiveMember();

    assertEquals(12L, result);
  }

  @Test
  void countAllInactiveMember_shouldReturnRepositoryCount() {
    when(userRepository.countByRoleAndMembershipExpiredAtBeforeAndEnabledIsTrue(
        any(), any(Instant.class)))
        .thenReturn(8L);

    long result = memberService.countAllInactiveMember();

    assertEquals(8L, result);
  }

  @Test
  void createMember_shouldSaveMember_whenOtpIsValid() {
    MemberCreateRequest request = mock(MemberCreateRequest.class);
    Otp otp = mock(Otp.class);

    when(request.getEmail()).thenReturn("member@example.com");
    when(request.getCode()).thenReturn("123456");
    when(request.getName()).thenReturn("Test Member");
    when(request.getPhoneNumber()).thenReturn("08123456789");
    when(request.getPassword()).thenReturn("password123");

    when(otpService.findByEmailAndCode("member@example.com", "123456"))
        .thenReturn(otp);

    when(otp.getType()).thenReturn(OtpType.EMAIL_VERIFICATION);
    when(otp.getExpiredAt()).thenReturn(Instant.now().plusSeconds(300));

    when(passwordEncoder.encode("password123"))
        .thenReturn("encoded-password");

    memberService.createMember(request);

    verify(otp).setExpiredAt(any(Instant.class));
    verify(otpService).save(otp);

    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createMember_shouldThrowException_whenOtpTypeIsInvalid() {
    MemberCreateRequest request = mock(MemberCreateRequest.class);
    Otp otp = mock(Otp.class);

    when(request.getEmail()).thenReturn("member@example.com");
    when(request.getCode()).thenReturn("123456");

    when(otpService.findByEmailAndCode("member@example.com", "123456"))
        .thenReturn(otp);

    when(otp.getType()).thenReturn(OtpType.RESET_PASSWORD);

    assertThrows(
        BadRequestException.class,
        () -> memberService.createMember(request));

    verify(userRepository, never()).save(any());
  }

  @Test
  void createMember_shouldThrowException_whenOtpIsExpired() {
    MemberCreateRequest request = mock(MemberCreateRequest.class);
    Otp otp = mock(Otp.class);

    when(request.getEmail()).thenReturn("member@example.com");
    when(request.getCode()).thenReturn("123456");

    when(otpService.findByEmailAndCode("member@example.com", "123456"))
        .thenReturn(otp);

    when(otp.getType()).thenReturn(OtpType.EMAIL_VERIFICATION);
    when(otp.getExpiredAt()).thenReturn(Instant.now().minusSeconds(60));

    assertThrows(
        BadRequestException.class,
        () -> memberService.createMember(request));

    verify(userRepository, never()).save(any());
  }

  @Test
  void updateMember_shouldUpdateMemberNameAndEmail() {
    MemberUpdateRequest request = mock(MemberUpdateRequest.class);
    User member = mock(User.class);

    when(userService.findById(memberId)).thenReturn(member);

    when(request.getName()).thenReturn("Updated Name");
    when(request.getEmail()).thenReturn("updated@example.com");
    when(request.getPhoneNumber()).thenReturn(null);
    when(request.getPassword()).thenReturn(null);
    when(request.getPhoto()).thenReturn(null);

    memberService.updateMember(memberId.toString(), request);

    verify(member).setName("Updated Name");
    verify(member).setEmail("updated@example.com");
    verify(userRepository).save(member);
  }

  @Test
  void deleteMember_shouldSoftDeleteMember() {
    User member = mock(User.class);

    when(userService.findById(memberId)).thenReturn(member);

    memberService.deleteMember(memberId.toString());

    verify(member).setMembershipExpiredAt(null);
    verify(member).setDeletedAt(any(Instant.class));
    verify(member).setEnabled(false);

    verify(userRepository).save(member);
  }
}

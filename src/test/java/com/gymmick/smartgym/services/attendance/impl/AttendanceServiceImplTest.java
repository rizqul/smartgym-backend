package com.gymmick.smartgym.services.attendance.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gymmick.smartgym.dto.attendance.AttendingResponse;
import com.gymmick.smartgym.dto.attendance.MemberAttendanceSummaryResponse;
import com.gymmick.smartgym.entity.Attendance;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.AttendingMethod;
import com.gymmick.smartgym.entity.enums.AttendingStatus;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.mappers.AttendanceMapping;
import com.gymmick.smartgym.repositories.AttendanceRepository;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.utils.JwtToken;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

  private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");

  @Mock
  private AttendanceRepository attendanceRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private AttendanceMapping attendanceMapping;
  @Mock
  private JwtToken jwtToken;

  @InjectMocks
  private AttendanceServiceImpl attendanceService;

  private User member;

  @BeforeEach
  void setUp() {
    member = User.builder()
        .id(UUID.randomUUID())
        .name("Test Member")
        .email("member@example.com")
        .password("not-a-real-password")
        .role(UserRole.MEMBER)
        .build();
  }

  @Test
  void getMyAttendanceSummary_whenMemberHasNoAttendance_returnsZeroSummary() {
    when(attendanceRepository.findAllByMemberAndStatusOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED)).thenReturn(List.of());

    MemberAttendanceSummaryResponse result = attendanceService.getMyAttendanceSummary(member);

    assertAll(
        () -> assertEquals(0, result.getTotalGymMinutes()),
        () -> assertEquals(0f, result.getTotalGymHours()),
        () -> assertEquals(0, result.getCurrentStreak()),
        () -> assertEquals(0, result.getLongestStreak()),
        () -> assertEquals(0, result.getTotalVisits()),
        () -> assertNull(result.getLastCheckInAt()),
        () -> assertFalse(result.isCheckedInToday()));
  }

  @Test
  void getMyAttendanceSummary_sumsOnlyCompletedPositiveDurationVisits() {
    Attendance first = attendanceOn(LocalDate.now(JAKARTA).minusDays(1), 90, true);
    Attendance second = attendanceOn(LocalDate.now(JAKARTA).minusDays(2), 45, true);
    Attendance currentlyCheckedIn = attendanceOn(LocalDate.now(JAKARTA), 0, false);
    Attendance invalidDuration = attendanceOn(LocalDate.now(JAKARTA).minusDays(3), -20, true);

    when(attendanceRepository.findAllByMemberAndStatusOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED))
        .thenReturn(List.of(currentlyCheckedIn, first, second, invalidDuration));

    MemberAttendanceSummaryResponse result = attendanceService.getMyAttendanceSummary(member);

    assertEquals(135, result.getTotalGymMinutes());
    assertEquals(2.25f, result.getTotalGymHours(), 0.001f);
    assertEquals(4, result.getTotalVisits());
  }

  @Test
  void getMyAttendanceSummary_countsCurrentAndLongestStreakWhenTodayIsIncluded() {
    LocalDate today = LocalDate.now(JAKARTA);
    List<Attendance> visits = List.of(
        attendanceOn(today, 60, true),
        attendanceOn(today.minusDays(1), 60, true),
        attendanceOn(today.minusDays(2), 60, true),
        attendanceOn(today.minusDays(5), 60, true),
        attendanceOn(today.minusDays(6), 60, true));

    when(attendanceRepository.findAllByMemberAndStatusOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED)).thenReturn(visits);

    MemberAttendanceSummaryResponse result = attendanceService.getMyAttendanceSummary(member);

    assertEquals(3, result.getCurrentStreak());
    assertEquals(3, result.getLongestStreak());
    assertTrue(result.isCheckedInToday());
  }

  @Test
  void getMyAttendanceSummary_resetsCurrentStreakWhenMemberDidNotAttendToday() {
    LocalDate yesterday = LocalDate.now(JAKARTA).minusDays(1);
    List<Attendance> visits = List.of(
        attendanceOn(yesterday, 60, true),
        attendanceOn(yesterday.minusDays(1), 60, true),
        attendanceOn(yesterday.minusDays(2), 60, true));

    when(attendanceRepository.findAllByMemberAndStatusOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED)).thenReturn(visits);

    MemberAttendanceSummaryResponse result = attendanceService.getMyAttendanceSummary(member);

    assertEquals(0, result.getCurrentStreak());
    assertEquals(3, result.getLongestStreak());
    assertFalse(result.isCheckedInToday());
  }

  @Test
  void checkOut_whenMemberHasActiveAttendance_savesCheckoutAndReturnsMappedResponse() {
    Attendance activeAttendance = attendanceOn(LocalDate.now(JAKARTA), 0, false);
    AttendingResponse expectedResponse = AttendingResponse.builder().id(UUID.randomUUID()).build();

    when(userRepository.findByIdForUpdate(member.getId())).thenReturn(Optional.of(member));
    when(attendanceRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED)).thenReturn(Optional.of(activeAttendance));
    when(attendanceRepository.save(activeAttendance)).thenReturn(activeAttendance);
    when(attendanceMapping.toResponse(activeAttendance)).thenReturn(expectedResponse);

    AttendingResponse result = attendanceService.checkOut(member.getId());

    assertSame(expectedResponse, result);
    assertNotNull(activeAttendance.getCheckOutAt());
    assertTrue(activeAttendance.getCheckOutAt().isAfter(activeAttendance.getCheckInAt()));
    verify(attendanceRepository).save(activeAttendance);
  }

  @Test
  void checkOut_whenUserDoesNotExist_throwsResourceNotFound() {
    when(userRepository.findByIdForUpdate(member.getId())).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> attendanceService.checkOut(member.getId()));

    verifyNoInteractions(attendanceRepository, attendanceMapping);
  }

  @Test
  void checkOut_whenUserIsNotMember_throwsResourceNotFound() {
    member.setRole(UserRole.STAFF);
    when(userRepository.findByIdForUpdate(member.getId())).thenReturn(Optional.of(member));

    assertThrows(ResourceNotFoundException.class,
        () -> attendanceService.checkOut(member.getId()));

    verify(attendanceRepository, never()).save(any(Attendance.class));
  }

  @Test
  void checkOut_whenNoActiveAttendance_throwsBadRequest() {
    when(userRepository.findByIdForUpdate(member.getId())).thenReturn(Optional.of(member));
    when(attendanceRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED)).thenReturn(Optional.empty());

    assertThrows(BadRequestException.class,
        () -> attendanceService.checkOut(member.getId()));

    verify(attendanceRepository, never()).save(any(Attendance.class));
  }

  private Attendance attendanceOn(LocalDate date, long durationMinutes, boolean checkedOut) {
    Instant checkIn = date.atTime(12, 0).atZone(JAKARTA).toInstant();
    Instant checkOut = checkedOut ? checkIn.plusSeconds(durationMinutes * 60) : null;
    return Attendance.builder()
        .id(UUID.randomUUID())
        .member(member)
        .method(AttendingMethod.QR)
        .status(AttendingStatus.ACCEPTED)
        .checkInAt(checkIn)
        .checkOutAt(checkOut)
        .build();
  }
}

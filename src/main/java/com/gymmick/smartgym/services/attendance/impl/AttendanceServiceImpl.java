package com.gymmick.smartgym.services.attendance.impl;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.attendance.*;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.entity.Attendance;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.*;
import com.gymmick.smartgym.mappers.AttendanceMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.attendance.AttendanceService;
import com.gymmick.smartgym.specification.AttendanceSpecification;
import com.gymmick.smartgym.utils.JwtToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceServiceImpl implements AttendanceService {

  private final AttendanceRepository attendanceRepository;
  private final UserRepository userRepository;

  private final AttendanceMapping attendanceMapping;

  private final JwtToken jwtToken;

  @Override
  public AttendanceSummaryResponse getSummary() {
    LocalDate today = LocalDate.now(ZoneId.systemDefault());
    Instant startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
    Instant startOfTomorrow = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

    return AttendanceSummaryResponse.builder()
        .historyCount(attendanceRepository.count())
        .checkinsToday(attendanceRepository.countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(
            startOfToday, startOfTomorrow))
        .activeNow(attendanceRepository.countByStatusAndCheckOutAtIsNull(AttendingStatus.ACCEPTED))
        .build();
  }

  @Override
  public AttendanceHourlyChartResponse getHourlyChart() {
    ZoneId zone = ZoneId.systemDefault();
    ZonedDateTime now = ZonedDateTime.now(zone);

    int currentSlotHour = (now.getHour() / 2) * 2;
    ZonedDateTime currentSlotStart = now.toLocalDate()
        .atTime(currentSlotHour, 0)
        .atZone(zone);

    List<AttendanceHourlySlot> slots = new ArrayList<>();

    for (int i = 11; i >= 0; i--) {
      ZonedDateTime slotZdt = currentSlotStart.minusHours(i * 2L);
      Instant slotStart = slotZdt.toInstant();
      Instant slotEnd = slotStart.plus(Duration.ofHours(2));

      long todayCount = attendanceRepository
          .countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(slotStart, slotEnd);

      Instant historyRangeStart = slotZdt.minusWeeks(1).toInstant();
      Instant historyRangeEnd = historyRangeStart.plus(Duration.ofHours(2));

      long lastWeekCount = attendanceRepository
          .countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(historyRangeStart, historyRangeEnd);

      slots.add(AttendanceHourlySlot.builder()
          .label(String.format("%02d:00", slotZdt.getHour()))
          .todayCount(todayCount)
          .lastWeekCount(lastWeekCount)
          .build());
    }

    return AttendanceHourlyChartResponse.builder()
        .date(now.toLocalDate().toString())
        .slots(slots)
        .build();
  }

  @Override
  public PaginationResponse<AttendingResponse> findAllMyAttendance(User member, AttendanceFilterRequest request) {
    Integer page = request.getPage();
    Integer size = request.getSize();

    Specification<Attendance> specification = AttendanceSpecification.getMyAttendance(member, request);

    Sort sortBy = Sort.by(Attendance::getCheckInAt).descending();
    PageRequest pageRequest = PageRequest.of(page, size, sortBy);

    Page<Attendance> attendancePage = attendanceRepository.findAll(specification, pageRequest);
    List<AttendingResponse> attendances = attendanceMapping.toListResponses(attendancePage.getContent());

    PaginationMeta pagination = PaginationLinkBuilder.build(page, attendancePage.getTotalPages(), size,
        attendancePage.getTotalElements(), p -> ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQueryParam("page", p)
            .toUriString());

    return PaginationResponse.<AttendingResponse>builder()
        .data(attendances)
        .pagination(pagination)
        .build();
  }

  @Override
  public List<AttendingResponse> findRecentAttending(String limit) {

    Integer sizeLimit = NumberUtils.toInt(limit, 10);

    List<Attendance> attendances = attendanceRepository
        .findAllByOrderByCheckInAtDesc(PageRequest.of(0, sizeLimit > 100 ? 100 : sizeLimit));
    return attendanceMapping.toListResponses(attendances);
  }

  @Override
  public AttendingResponse findActiveCheckIn(User member) {
    Attendance attendance = attendanceRepository
        .findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(member, AttendingStatus.ACCEPTED)
        .orElseThrow(() -> new BadRequestException("You haven't checked in today!"));

    return attendanceMapping.toResponse(attendance);
  }

  @Override
  public AttendanceTokenResponse generateQrToken() {
    String token = jwtToken.createAttendanceCheckInToken(Duration.ofHours(1));
    Instant expiresAt = jwtToken.verifyToken(token).getExpiresAt();

    return AttendanceTokenResponse.builder()
        .token(token)
        .expiresAt(expiresAt)
        .expiresInSeconds(expiresAt.getEpochSecond() - Instant.now().getEpochSecond())
        .build();
  }

  @Override
  @Transactional
  public AttendingResponse checkIn(String token, User member) {
    var claims = jwtToken.verifyToken(token);

    if (!claims.getClaimAsString("purpose").equals("checkin")) {
      throw new BadRequestException("QR check-in tidak valid!");
    }

    member = userRepository.findByIdForUpdate(member.getId())
        .orElseThrow(() -> new BadRequestException("Member tidak ditemukan!"));

    if (member.getMembershipExpiredAt() == null || member.getMembershipExpiredAt().isBefore(Instant.now())) {
      Attendance attendance = Attendance.builder()
          .member(member)
          .method(AttendingMethod.QR)
          .status(AttendingStatus.DECLINED)
          .checkInAt(Instant.now())
          .denialReason("Membership tidak aktif atau telah berakhir.")
          .build();

      attendanceRepository.save(attendance);

      throw new BadRequestException("Anda belum mengaktifkan membership atau membership anda telah berakhir!");
    }

    attendanceRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
        member, AttendingStatus.ACCEPTED)
        .ifPresent(attendance -> {
          throw new BadRequestException("Anda telah melakukan check-in sebelumnya!");
        });

    Attendance attendance = Attendance.builder()
        .member(member)
        .method(AttendingMethod.QR)
        .status(AttendingStatus.ACCEPTED)
        .checkInAt(Instant.now())
        .build();

    attendance = attendanceRepository.save(attendance);

    return attendanceMapping.toResponse(attendance);
  }

  @Override
  @Transactional
  public AttendingResponse checkOut(UUID memberId) {
    User member = userRepository.findByIdForUpdate(memberId)
        .orElseThrow(() -> new ResourceNotFoundException("Member tidak ditemukan!"));

    if (member.getRole() != UserRole.MEMBER) {
      throw new ResourceNotFoundException("Member tidak ditemukan!");
    }

    Attendance attendance = attendanceRepository
        .findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
            member, AttendingStatus.ACCEPTED)
        .orElseThrow(() -> new BadRequestException("Anda belum melakukan check-in!"));

    attendance.setCheckOutAt(Instant.now());
    attendance = attendanceRepository.save(attendance);

    return attendanceMapping.toResponse(attendance);
  }

  @Override
  public MemberAttendanceSummaryResponse getMyAttendanceSummary(User member) {

    ZoneId zone = ZoneId.of("Asia/Jakarta");
    LocalDate today = LocalDate.now(zone);

    List<Attendance> attendances = attendanceRepository.findAllByMemberAndStatusOrderByCheckInAtDesc(
        member,
        AttendingStatus.ACCEPTED);

    // 1. Calculate total completed gym duration
    long totalGymMinutes = attendances.stream()
        .filter(a -> a.getCheckOutAt() != null)
        .mapToLong(a -> ChronoUnit.MINUTES.between(
            a.getCheckInAt(),
            a.getCheckOutAt()))
        .filter(minutes -> minutes > 0)
        .sum();

    // 2. Get unique attendance dates
    Set<LocalDate> attendanceDates = new TreeSet<>(
        java.util.Comparator.reverseOrder());

    attendances.forEach(attendance -> attendanceDates.add(
        attendance.getCheckInAt()
            .atZone(zone)
            .toLocalDate()));

    // 3. Calculate current streak
    long currentStreak = 0;

    if (attendanceDates.contains(today)) {

      LocalDate expectedDate = today;

      for (LocalDate date : attendanceDates) {

        if (!date.equals(expectedDate)) {
          break;
        }

        currentStreak++;
        expectedDate = expectedDate.minusDays(1);
      }
    }

    // 4. Calculate longest streak
    long longestStreak = 0;
    long streak = 0;

    LocalDate previousDate = null;

    for (LocalDate date : new TreeSet<>(attendanceDates)) {

      if (previousDate != null &&
          date.equals(previousDate.plusDays(1))) {

        streak++;

      } else {

        streak = 1;
      }

      longestStreak = Math.max(longestStreak, streak);
      previousDate = date;
    }

    // 5. Get latest check-in
    Instant lastCheckInAt = attendances.isEmpty()
        ? null
        : attendances.get(0).getCheckInAt();

    return MemberAttendanceSummaryResponse.builder()
        .totalGymMinutes(totalGymMinutes)
        .totalGymHours(totalGymMinutes / 60)
        .currentStreak(currentStreak)
        .longestStreak(longestStreak)
        .totalVisits(attendances.size())
        .lastCheckInAt(lastCheckInAt)
        .checkedInToday(attendanceDates.contains(today))
        .build();
  }
}

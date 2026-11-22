package com.gymmick.smartgym.services.checkin.impl;

import java.time.*;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.checkin.*;
import com.gymmick.smartgym.entity.Checkin;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.CheckinMethod;
import com.gymmick.smartgym.entity.enums.CheckinStatus;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.repositories.CheckinRepository;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.checkin.CheckinService;
import com.gymmick.smartgym.utils.JwtToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckinServiceImpl implements CheckinService {

    private static final Duration QR_TOKEN_LIFETIME = Duration.ofHours(1);
    private static final int MAX_RECENT_CHECKINS = 100;
    private static final ZoneId GYM_TIME_ZONE = ZoneId.of("Asia/Jakarta");
    private static final String INACTIVE_MEMBERSHIP_REASON = "Membership Anda tidak aktif atau telah kedaluwarsa.";

    private final CheckinRepository checkinRepository;
    private final UserRepository userRepository;
    private final JwtToken jwtToken;

    @Override
    public CheckinTokenResponse generateQrToken() {
        String token = jwtToken.createCheckinToken(QR_TOKEN_LIFETIME);
        Instant expiresAt = jwtToken.verifyToken(token).getExpiresAt();

        return CheckinTokenResponse.builder()
                .token(token)
                .expiresAt(expiresAt)
                .expiresInSeconds(QR_TOKEN_LIFETIME.toSeconds())
                .build();
    }

    @Override
    @Transactional
    public CheckinResponse checkIn(String token, User member) {
        var claims = jwtToken.verifyToken(token);
        if (!"checkin".equals(claims.getClaimAsString("purpose"))) {
            throw new BadRequestException("QR check-in tidak valid!");
        }

        User lockedMember = userRepository.findByIdForUpdate(member.getId())
                .orElseThrow(() -> new BadRequestException("Data member tidak ditemukan."));
        Instant now = Instant.now();

        var activeCheckin = checkinRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
                lockedMember, CheckinStatus.GRANTED);
        if (activeCheckin.isPresent()) {
            return toResponse(activeCheckin.get(), true);
        }

        boolean membershipActive = lockedMember.getMembershipExpiredAt() != null
                && lockedMember.getMembershipExpiredAt().isAfter(now);
        if (!membershipActive) {
            var lastCheckin = checkinRepository.findFirstByMemberOrderByCheckInAtDesc(lockedMember);
            if (lastCheckin.isPresent()
                    && lastCheckin.get().getStatus() == CheckinStatus.DENIED
                    && INACTIVE_MEMBERSHIP_REASON.equals(lastCheckin.get().getDenialReason())) {
                return toResponse(lastCheckin.get(), true);
            }

            Checkin deniedCheckin = Checkin.builder()
                    .member(lockedMember)
                    .method(CheckinMethod.QR)
                    .status(CheckinStatus.DENIED)
                    .checkInAt(now)
                    .denialReason(INACTIVE_MEMBERSHIP_REASON)
                    .build();

            return toResponse(checkinRepository.save(deniedCheckin), false);
        }

        Checkin checkin = Checkin.builder()
                .member(lockedMember)
                .method(CheckinMethod.QR)
                .status(CheckinStatus.GRANTED)
                .checkInAt(now)
                .build();

        return toResponse(checkinRepository.save(checkin), false);
    }

    @Override
    @Transactional
    public CheckinResponse checkOut(UUID memberId) {
        User lockedMember = userRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member tidak ditemukan."));
        if (lockedMember.getRole() != UserRole.MEMBER) {
            throw new ResourceNotFoundException("Member tidak ditemukan.");
        }

        Checkin checkin = checkinRepository
                .findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
                        lockedMember, CheckinStatus.GRANTED)
                .orElseThrow(() -> new BadRequestException("Anda belum check-in di gym."));

        checkin.setCheckOutAt(Instant.now());
        return toResponse(checkinRepository.save(checkin), false);
    }

    @Override
    public List<CheckinResponse> findRecentCheckins(int limit) {
        if (limit < 1 || limit > MAX_RECENT_CHECKINS) {
            throw new BadRequestException("Limit riwayat check-in harus antara 1 dan 100.");
        }

        return checkinRepository.findAllByOrderByCheckInAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CheckinSummaryResponse getSummary() {
        LocalDate today = LocalDate.now(GYM_TIME_ZONE);
        Instant startOfToday = today.atStartOfDay(GYM_TIME_ZONE).toInstant();
        Instant startOfTomorrow = today.plusDays(1).atStartOfDay(GYM_TIME_ZONE).toInstant();

        return CheckinSummaryResponse.builder()
                .historyCount(checkinRepository.count())
                .checkinsToday(checkinRepository.countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(
                        startOfToday, startOfTomorrow))
                .onFloor(checkinRepository.countByStatusAndCheckOutAtIsNull(CheckinStatus.GRANTED))
                .build();
    }

    private CheckinResponse toResponse(Checkin checkin) {
        return toResponse(checkin, false);
    }

    private CheckinResponse toResponse(Checkin checkin, boolean duplicate) {
        User member = checkin.getMember();

        return CheckinResponse.builder()
                .id(checkin.getId())
                .memberId(member.getId())
                .memberName(member.getName())
                .membershipExpiresAt(member.getMembershipExpiredAt())
                .method(checkin.getMethod())
                .status(checkin.getStatus())
                .checkInAt(checkin.getCheckInAt())
                .checkOutAt(checkin.getCheckOutAt())
                .denialReason(checkin.getDenialReason())
                .duplicate(duplicate)
                .build();
    }
}

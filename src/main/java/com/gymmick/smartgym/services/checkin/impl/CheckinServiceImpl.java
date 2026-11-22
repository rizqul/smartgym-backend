package com.gymmick.smartgym.services.checkin.impl;

import java.time.*;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.dto.checkin.*;
import com.gymmick.smartgym.entity.Checkin;
import com.gymmick.smartgym.entity.Membership;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.CheckinMethod;
import com.gymmick.smartgym.entity.enums.CheckinStatus;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.repositories.CheckinRepository;
import com.gymmick.smartgym.repositories.MembershipRepository;
import com.gymmick.smartgym.services.checkin.CheckinService;
import com.gymmick.smartgym.utils.JwtToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckinServiceImpl implements CheckinService {

    private static final Duration QR_TOKEN_LIFETIME = Duration.ofHours(1);
    private static final int MAX_RECENT_CHECKINS = 100;

    private final CheckinRepository checkinRepository;
    private final MembershipRepository membershipRepository;
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

        Instant now = Instant.now();

        if (member.getMembershipExpiredAt() == null || member.getMembershipExpiredAt().isBefore(now)) {
            throw new BadRequestException("Membership Anda tidak aktif atau telah kedaluwarsa.");
        }

        Checkin checkin = Checkin.builder()
                .member(member)
                .method(CheckinMethod.QR)
                .status(CheckinStatus.GRANTED)
                .checkInAt(now)
                .build();

        return toResponse(checkinRepository.save(checkin));
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
        Instant startOfToday = LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        return CheckinSummaryResponse.builder()
                .historyCount(checkinRepository.count())
                .checkinsToday(checkinRepository.countByCheckInAtGreaterThanEqual(startOfToday))
                .onFloor(checkinRepository.countByStatusAndCheckOutAtIsNull(CheckinStatus.GRANTED))
                .build();
    }

    private CheckinResponse toResponse(Checkin checkin) {
        User member = checkin.getMember();

        return CheckinResponse.builder()
                .id(checkin.getId())
                .memberId(member.getId())
                .memberName(member.getName())
                .membershipExpiresAt(member.getMembershipExpiredAt())
                .method(checkin.getMethod())
                .status(checkin.getStatus())
                .checkInAt(checkin.getCheckInAt())
                .denialReason(checkin.getDenialReason())
                .build();
    }
}

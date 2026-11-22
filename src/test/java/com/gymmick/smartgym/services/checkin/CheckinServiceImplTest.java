package com.gymmick.smartgym.services.checkin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import com.gymmick.smartgym.entity.Checkin;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.CheckinStatus;
import com.gymmick.smartgym.repositories.CheckinRepository;
import com.gymmick.smartgym.repositories.UserRepository;
import com.gymmick.smartgym.services.checkin.impl.CheckinServiceImpl;
import com.gymmick.smartgym.utils.JwtToken;

@ExtendWith(MockitoExtension.class)
class CheckinServiceImplTest {

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtToken jwtToken;

    @InjectMocks
    private CheckinServiceImpl checkinService;

    private User member;

    @BeforeEach
    void setUp() {
        member = User.builder()
                .id(UUID.randomUUID())
                .name("Member")
                .membershipExpiredAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private void stubAuthenticatedMember() {
        when(jwtToken.verifyToken("qr-token")).thenReturn(
                Jwt.withTokenValue("qr-token").header("alg", "HS256").claim("purpose", "checkin").build());
        when(userRepository.findByIdForUpdate(member.getId())).thenReturn(Optional.of(member));
    }

    @Test
    void repeatedCheckinReturnsExistingEntryWithoutSavingAnother() {
        stubAuthenticatedMember();
        Checkin activeCheckin = Checkin.builder()
                .id(UUID.randomUUID())
                .member(member)
                .status(CheckinStatus.GRANTED)
                .checkInAt(Instant.now().minusSeconds(60))
                .build();
        when(checkinRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
                member, CheckinStatus.GRANTED)).thenReturn(Optional.of(activeCheckin));

        var response = checkinService.checkIn("qr-token", member);

        assertTrue(response.isDuplicate());
        assertEquals(activeCheckin.getId(), response.getId());
        verify(checkinRepository, never()).save(any(Checkin.class));
    }

    @Test
    void inactiveMembershipCreatesDeniedEntryAndRepeatedScanDoesNotCreateAnother() {
        stubAuthenticatedMember();
        member.setMembershipExpiredAt(Instant.now().minusSeconds(60));
        Checkin deniedCheckin = Checkin.builder()
                .id(UUID.randomUUID())
                .member(member)
                .status(CheckinStatus.DENIED)
                .checkInAt(Instant.now())
                .denialReason("Membership Anda tidak aktif atau telah kedaluwarsa.")
                .build();
        when(checkinRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
                member, CheckinStatus.GRANTED)).thenReturn(Optional.empty());
        when(checkinRepository.findFirstByMemberOrderByCheckInAtDesc(member))
                .thenReturn(Optional.empty(), Optional.of(deniedCheckin));
        when(checkinRepository.save(any(Checkin.class))).thenReturn(deniedCheckin);

        var firstResponse = checkinService.checkIn("qr-token", member);
        var repeatedResponse = checkinService.checkIn("qr-token", member);

        assertEquals(CheckinStatus.DENIED, firstResponse.getStatus());
        assertEquals(deniedCheckin.getDenialReason(), firstResponse.getDenialReason());
        assertTrue(repeatedResponse.isDuplicate());
        verify(checkinRepository, times(1)).save(any(Checkin.class));
    }

    @Test
    void checkOutClosesActiveCheckin() {
        when(userRepository.findByIdForUpdate(member.getId())).thenReturn(Optional.of(member));
        Checkin activeCheckin = Checkin.builder()
                .id(UUID.randomUUID())
                .member(member)
                .status(CheckinStatus.GRANTED)
                .checkInAt(Instant.now().minusSeconds(60))
                .build();
        when(checkinRepository.findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
                member, CheckinStatus.GRANTED)).thenReturn(Optional.of(activeCheckin));
        when(checkinRepository.save(activeCheckin)).thenReturn(activeCheckin);

        var response = checkinService.checkOut(member);

        assertNotNull(response.getCheckOutAt());
        verify(checkinRepository).save(activeCheckin);
    }

    @Test
    void summaryUsesJakartaCalendarDayRegardlessOfJvmTimezone() {
        var originalTimezone = java.util.TimeZone.getDefault();
        try {
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
            when(checkinRepository.count()).thenReturn(0L);
            when(checkinRepository.countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(any(), any()))
                    .thenReturn(0L);
            when(checkinRepository.countByStatusAndCheckOutAtIsNull(CheckinStatus.GRANTED)).thenReturn(0L);

            checkinService.getSummary();

            ArgumentCaptor<Instant> start = ArgumentCaptor.forClass(Instant.class);
            ArgumentCaptor<Instant> end = ArgumentCaptor.forClass(Instant.class);
            verify(checkinRepository).countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(
                    start.capture(), end.capture());
            assertEquals(ZoneId.of("Asia/Jakarta").getRules().getOffset(start.getValue()).getTotalSeconds(),
                    7 * 60 * 60);
            assertEquals(24 * 60 * 60, end.getValue().getEpochSecond() - start.getValue().getEpochSecond());
        } finally {
            java.util.TimeZone.setDefault(originalTimezone);
        }
    }
}

package com.gymmick.smartgym.repositories;

import java.time.Instant;
import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID>, JpaSpecificationExecutor<Attendance> {
    Optional<Attendance> findFirstByMemberAndStatusAndCheckOutAtIsNullOrderByCheckInAtDesc(
            User member, AttendingStatus status);

    Optional<Attendance> findFirstByMemberOrderByCheckInAtDesc(User member);

    List<Attendance> findAllByOrderByCheckInAtDesc(Pageable pageable);

    long countByCheckInAtGreaterThanEqualAndCheckInAtLessThan(Instant since, Instant before);

    long countByStatusAndCheckOutAtIsNull(AttendingStatus status);
}

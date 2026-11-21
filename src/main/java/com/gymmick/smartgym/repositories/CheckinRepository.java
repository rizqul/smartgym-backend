package com.gymmick.smartgym.repositories;

import com.gymmick.smartgym.entity.Checkin;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.CheckinStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckinRepository extends JpaRepository<Checkin, UUID>, JpaSpecificationExecutor<Checkin> {
    Optional<Checkin> findByMemberAndCheckOutAtIsNull(User member);

    List<Checkin> findAllByOrderByCheckInAtDesc(Pageable pageable);

    long countByCheckInAtGreaterThanEqual(Instant since);

    long countByStatusAndCheckOutAtIsNull(CheckinStatus status);
}

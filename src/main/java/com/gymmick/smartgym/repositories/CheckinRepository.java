package com.gymmick.smartgym.repositories;

import com.gymmick.smartgym.entity.Checkin;
import com.gymmick.smartgym.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CheckinRepository extends JpaRepository<Checkin, UUID>, JpaSpecificationExecutor<Checkin> {
    Optional<Checkin> findByMemberAndCheckOutAtIsNull(User member);
}

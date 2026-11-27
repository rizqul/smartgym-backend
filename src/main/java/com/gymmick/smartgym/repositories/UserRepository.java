package com.gymmick.smartgym.repositories;

import java.time.Instant;
import java.util.*;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByIdAndEnabledIsTrue(UUID id);

    Optional<User> findByEmailAndEnabledIsTrue(String email);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select user from User user where user.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") UUID id);

    long countByRoleAndEnabledIsTrue(UserRole role);

    long countByRoleAndMembershipExpiredAtBeforeAndEnabledIsTrue(UserRole role, Instant now);

    long countByRoleAndMembershipExpiredAtAfterAndEnabledIsTrue(UserRole role, Instant now);

    long countByRoleAndCreatedAtBetweenAndEnabledIsTrue(UserRole role, Instant start, Instant end);
}

package com.gymmick.smartgym.repositories;

import java.time.Instant;
import java.util.*;

import com.gymmick.smartgym.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Membership;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    @Query("SELECT m FROM Membership m WHERE m.transaction.id = :id")
    Optional<Membership> findByTransactionId(UUID id);

    @Query("SELECT m FROM Membership m " +
            "JOIN m.transaction t " +
            "WHERE m.member.id = :memberId " +
            "AND :now BETWEEN m.startDate AND m.endDate " +
            "AND t.status = :status")
    List<Membership> findCurrentMemberships(
            @Param("memberId") UUID memberId,
            @Param("now") Instant now,
            @Param("status") TransactionStatus status
    );
}

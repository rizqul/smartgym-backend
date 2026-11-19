package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Membership;
import org.springframework.data.jpa.repository.Query;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    @Query("SELECT m FROM Membership m WHERE m.transaction.id = :id")
    Optional<Membership> findByTransactionId(UUID id);
}

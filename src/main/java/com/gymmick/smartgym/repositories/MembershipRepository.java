package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymmick.smartgym.entity.Membership;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    Optional<Membership> findByTransactionId(UUID id);
}

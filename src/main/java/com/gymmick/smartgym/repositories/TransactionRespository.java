package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gymmick.smartgym.entity.Transaction;

public interface TransactionRespository
        extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByOrderId(String orderId);

    Optional<Transaction> findTopByMemberId(UUID id);

    @Query(value = "SELECT t.*, m.* FROM transactions t INNER JOIN membership_packages m ON t.package_id = m.id  WHERE t.id = :id", nativeQuery = true)
    Object findByMembershipPackageId(@Param("id") UUID id);
}

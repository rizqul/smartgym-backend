package com.gymmick.smartgym.repositories;

import java.time.Instant;
import java.util.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.entity.enums.TransactionType;

public interface TransactionRespository
        extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findByType(TransactionType type);

    Optional<Transaction> findByOrderId(String orderId);

    Optional<Transaction> findFirstByMemberIdAndStatusEqualsOrderByCreatedAtAsc(UUID memberId,
            TransactionStatus transactionStatus);

    List<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.type = :type AND t.status = :status
            """)
    Long sumAmountByTypeAndStatus(@Param("type") TransactionType type,
            @Param("status") TransactionStatus status);

    Long countByStatus(TransactionStatus status);

    @Query("""
              SELECT COALESCE(SUM(t.amount), 0)
              FROM Transaction t
              WHERE t.status = :status
                AND t.paidAt >= :startOfYear
                AND t.paidAt < :endOfYear
            """)
    Long sumAmountByStatusAndInRange(
            @Param("status") TransactionStatus status,
            @Param("startOfYear") Instant startOfYear,
            @Param("endOfYear") Instant endOfYear);
}

package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionStatus;

public interface TransactionRespository
        extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByOrderId(String orderId);

    Optional<Transaction> findFirstByMemberIdAndStatusEqualsOrderByCreatedAtAsc(UUID memberId,
            TransactionStatus transactionStatus);
}

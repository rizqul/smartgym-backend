package com.gymmick.smartgym.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.entity.enums.TransactionType;

public interface TransactionRespository
    extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

  Optional<Transaction> findByOrderId(String orderId);

  Optional<Transaction> findFirstByMemberIdAndStatusEqualsOrderByCreatedAtAsc(UUID memberId,
      TransactionStatus transactionStatus);

  Optional<Transaction> findFirstByMemberIdAndStatusEqualsAndTypeOrderByCreatedAtAsc(UUID memberId,
      TransactionStatus transactionStatus, TransactionType transactionType);
}

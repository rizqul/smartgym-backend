package com.gymmick.smartgym.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.transaction.TransactionFilterRequest;
import com.gymmick.smartgym.entity.Transaction;
import com.gymmick.smartgym.entity.enums.TransactionType;

import jakarta.persistence.criteria.Predicate;

public class TransactionSpecification {
  public static Specification<Transaction> getTransactionByTransactionType(TransactionFilterRequest request,
      TransactionType type) {
    return ((root, query, criteriaBuilder) -> {

      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.equal(root.get("type"), type.toString()));

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }

}

package com.gymmick.smartgym.specification;

import java.util.*;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.transaction.TransactionFilterRequest;
import com.gymmick.smartgym.entity.*;

import jakarta.persistence.criteria.Predicate;

public class TransactionSpecification {
    public static Specification<Transaction> getTransaction(TransactionFilterRequest request) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public static Specification<Transaction> getMyTransaction(User user, TransactionFilterRequest request) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("member"), user));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

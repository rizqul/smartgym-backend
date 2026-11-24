package com.gymmick.smartgym.specification;

import java.util.*;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.transaction.TransactionSearchRequest;
import com.gymmick.smartgym.entity.*;

import jakarta.persistence.criteria.Predicate;

public class TransactionSpecification {
    public static Specification<Transaction> getTransaction(TransactionSearchRequest request) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getSearch() != null && !request.getSearch().trim().isBlank()) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("orderId")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("name")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("email")),
                                        "%" + request.getSearch().toLowerCase() + "%")));
            }

            if (request.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public static Specification<Transaction> getMyTransaction(User user, TransactionSearchRequest request) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("member"), user));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

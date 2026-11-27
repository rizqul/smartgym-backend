package com.gymmick.smartgym.specification;

import java.time.Instant;
import java.util.*;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.user.UserSearchRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<User> getUserByRoleSpecification(UserSearchRequest request, UserRole role) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                "%" + request.getSearch().toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                                "%" + request.getSearch().toLowerCase() + "%")));
            }

            if (request.getMembership() != null) {

                if (request.getMembership()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("membershipExpiredAt"),
                            Instant.now()));
                } else {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.lessThan(root.get("membershipExpiredAt"),
                                    Instant.now()),
                            criteriaBuilder.isNull(root.get("membershipExpiredAt"))));
                }
            }

            predicates.add(criteriaBuilder.equal(root.get("role"), role.toString()));

            predicates.add(criteriaBuilder.equal(root.get("enabled"), true));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

package com.gymmick.smartgym.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.dashboard.UserFilterRequest;
import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<User> getUserByRoleSpecification(UserFilterRequest request, UserRole role) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null && !request.getName().trim().isEmpty()) {

                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + request.getName() + "%"));
            }

            predicates.add(criteriaBuilder.equal(root.get("role"), role.toString()));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

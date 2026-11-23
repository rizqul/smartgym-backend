package com.gymmick.smartgym.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.checkin.CheckinFilterRequest;
import com.gymmick.smartgym.entity.Checkin;
import com.gymmick.smartgym.entity.User;

import jakarta.persistence.criteria.Predicate;

public class CheckinSpecification {

    public static Specification<Checkin> getMyCheckins(User member, CheckinFilterRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("member").get("id"), member.getId()));

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

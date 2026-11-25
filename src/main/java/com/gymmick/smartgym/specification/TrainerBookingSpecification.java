package com.gymmick.smartgym.specification;

import java.util.*;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.trainer.TrainerBookingSearchRequest;
import com.gymmick.smartgym.entity.*;

import jakarta.persistence.criteria.Predicate;

public class TrainerBookingSpecification {
    public static Specification<TrainerBooking> getAllTrainerBookingSpecification(TrainerBookingSearchRequest request) {

        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("name")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("member").get("email")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("trainer").get("name")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("trainer").get("email")),
                                        "%" + request.getSearch().toLowerCase() + "%")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public static Specification<TrainerBooking> getMyTrainerBookingSpecification(User user,
            TrainerBookingSearchRequest request) {

        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("trainer").get("name")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("trainer").get("email")),
                                        "%" + request.getSearch().toLowerCase() + "%")));
            }

            predicates.add(criteriaBuilder.equal(root.get("member"), user));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

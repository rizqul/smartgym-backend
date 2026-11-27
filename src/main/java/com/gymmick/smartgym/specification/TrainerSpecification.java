package com.gymmick.smartgym.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.trainer.TrainerSearchRequest;
import com.gymmick.smartgym.entity.Trainer;

import jakarta.persistence.criteria.Predicate;

public class TrainerSpecification {
    public static Specification<Trainer> getTrainerSpecification(TrainerSearchRequest request) {
        return ((root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("specialization")),
                                        "%" + request.getSearch().toLowerCase() + "%")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}

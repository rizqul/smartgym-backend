package com.gymmick.smartgym.specification;

import java.util.*;

import org.springframework.data.jpa.domain.Specification;

import com.gymmick.smartgym.dto.trainer.TrainerBookingSearchRequest;
import com.gymmick.smartgym.entity.*;

import jakarta.persistence.criteria.Predicate;

<<<<<<< HEAD:src/main/java/com/gymmick/smartgym/specification/TrainerBookingSpecification.java
public class TrainerBookingSpecification {
    public static Specification<TrainerBooking> getAllTrainerBookingSpecification(TrainerBookingSearchRequest request) {

        return ((root, query, criteriaBuilder) -> {
=======
public class TransactionSpecification {
  public static Specification<Transaction> getTransaction(TransactionSearchRequest request) {
    return ((root, query, criteriaBuilder) -> {
>>>>>>> cdbef48 ([FIX] add enpoint to update and delete trainer. add sort for transaction):src/main/java/com/gymmick/smartgym/specification/TransactionSpecification.java

      List<Predicate> predicates = new ArrayList<>();

<<<<<<< HEAD:src/main/java/com/gymmick/smartgym/specification/TrainerBookingSpecification.java
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
=======
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
>>>>>>> cdbef48 ([FIX] add enpoint to update and delete trainer. add sort for transaction):src/main/java/com/gymmick/smartgym/specification/TransactionSpecification.java

      List<Predicate> predicates = new ArrayList<>();

<<<<<<< HEAD:src/main/java/com/gymmick/smartgym/specification/TrainerBookingSpecification.java
            if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("trainer").get("name")),
                                        "%" + request.getSearch().toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("trainer").get("email")),
                                        "%" + request.getSearch().toLowerCase() + "%")));
            }

            predicates.add(criteriaBuilder.equal(root.get("member"), user));
=======
      if (request.getType() != null) {
        predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
      }
>>>>>>> cdbef48 ([FIX] add enpoint to update and delete trainer. add sort for transaction):src/main/java/com/gymmick/smartgym/specification/TransactionSpecification.java

      if (request.getStatus() != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
      }

      predicates.add(criteriaBuilder.equal(root.get("member"), user));

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }
}
